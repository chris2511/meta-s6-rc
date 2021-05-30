# S6-RC: An alternative to SysV and Systemd

This layer introduces [s6-rc](https://skarnet.org/software/s6-rc) as init system.

## Quickstart

```sh
git clone git://git.yoctoproject.org/poky
(cd poky && git checkout -b dunfell origin/dunfell)
git clone https://github.com/chris2511/meta-s6-rc.git
. poky/oe-init-build-env
bitbake-layers add-layer ../meta-s6-rc
TCLIBC = "musl" INIT_MANAGER = "s6" bitbake core-image-minimal
runqemu serialstdio nographic kvm
```

Or as usual put them into your conf/local.conf:
```
TCLIBC = "musl"
INIT_MANAGER = "s6"
```

## Booting

The boot process follows the proposals of the S6 author.
To setup the system with `s6-linux-init-maker`
`recipes-rc/s6-init/files/init` will be run during first boot
and set up the system:

 1. Call `/bin/s6-linux-init-maker /etc/s6-linux-init/current`
 2. Moves `/etc/s6-linux-init/current/bin/init` to `/sbin/init`
    and thus replaces itself for the next boot.
 3. Moves the created [halt|poweroff|reboot|telinit].s6
    scripts to `/sbin/` and activates them via update-alternatives.
 4. Run `s6-rc-compile` on the current service tree in `/etc/s6-rc/tree´.
 5. Exec into the new `/sbin/init`, which execs into `s6-linux-init`

## Documentation

The S6 system itself is thoroughly documented by its author
and will not be repeated here.

This layer adds the `s6-rc.bbclass`, which allows to easily
create new services with dependencies. It populates the
`/etc/s6-rc/tree` directory with services declared by the following
variables:
 - S6RC\_BUNDLES and S6RC\_BUNDLE\_%
 - S6RC\_ONESHOTS and S6RC\_ONESHOT\_%[ ]
 - S6RC\_LONGRUNS and S6RC\_LONGRUN\_%[ ] S6RC\_LONGRUN\_%\_log[ ]

`s6-init.bb` shows how to use them in practice.
Please read/know about the concepts documented in
[s6-rc-compile](https://skarnet.org/software/s6-rc/s6-rc-compile.html)

### Bundles

`S6RC_BUNDLES` contains a space separated list of bundle-names.
Each `S6RC_BUNDLE_%` with % replaced by a bundle name contains a space separated
list of services and bundles that will be written to the
`contents` file of the bundle.

Example:
```
S6RC_BUNDLES = "bundleA bundleB"
S6RC_BUNDLE_bundleA = "service1 service2"
S6RC_BUNDLE_bundleB = "svx svy bundleA"
```
will result in
```
/etc/s6-rc-tree/bundleA/type (which contains 'bundle')
/etc/s6-rc-tree/bundleA/contents (which contains 'service1\nservice2')
/etc/s6-rc-tree/bundleB/type (which contains 'bundle')
/etc/s6-rc-tree/bundleB/contents (which contains 'svx\nsvy\nbundleA')
```

### Atomic services

All regular files recognized by s6-rc-compile can either be directly
added as `S6RC_ONESHOT_runner-a[dependencies] = "runner-b bundle-x"`
or provided as `<service-name>.<property>` file.
In this example a `runner-a.dependencies` will be used as runner-a/dependencies file verbatim.
This is especially useful for `up` and `run` files, which may become lengthy
and cumbersome to write in one line.

### Oneshots

`S6RC_ONESHOTS` contains a space separated list of oneshot-names
`S6RC_ONESHOT_%[ ]` declares properties with % replaced by a oneshot-name

Properties for oneshots are those documented in the [s6-rc-compile](https://skarnet.org/software/s6-rc/s6-rc-compile.html):

Example:
```
S6RC_ONESHOTS = "start mount-procsysdev networking"
S6RC_ONESHOT_start[up] = 'echo "init-stage2 starting."'
S6RC_ONESHOT_mount-procsysdev[dependencies] = "start"
```
will result in
```
/etc/s6-rc-tree/start/type (which contains 'oneshot')
/etc/s6-rc-tree/start/up (which contains 'echo "init-stage2 starting."')
/etc/s6-rc-tree/mount-procsysdev/type (which contains 'oneshot')
/etc/s6-rc-tree/mount-procsysdev/dependencies (which contains 'start')
```
An existing file `${S}/mount-procsysdev.up` will be copied verbatim to
`/etc/s6-rc-tree/mount-procsysdev/up`

### Longruns

`S6RC_LONGRUNS` contains a space separated list of longrun-names
`S6RC_LONGRUN_%[ ]` declares properties with % replaced by a longrun-name

The functionality and behavior is analog to the oneshots, just with the
longrun properties.

`S6RC_LONGRUN_%_log[]` properties are evaluated differently.
If at least one of them exists a log service will be created
with the following property files:
 - name: <longrun-name>-log
 - notification-fd: 3
 - consumer-for <longrun-name>
 - dependencies: mount-temp
 - run: `umask {umask} s6-setuidgid {user} s6-log -d3 {script} {dir}`
   with {...} replaced by overrideable defaults:
   - umask: 0037
   - user: logger
   - script: `T s100000 n10`
   - dir: `/var/log/<longrun-name>`

Example:
```
S6RC_LONGRUNS = "getty syslogd"
S6RC_LONGRUN_getty[run] = "fdmove -c 2 1 /sbin/getty -L 0 console 2>&1"
S6RC_LONGRUN_getty[dependencies] = "mount-procsysdev hostname"
S6RC_LONGRUN_getty[down-signal] = "9"
S6RC_LONGRUN_syslogd[dependencies] = "mount-procsysdev"
S6RC_LONGRUN_syslogd[notification-fd] = "3"
S6RC_LONGRUN_syslogd_log[user] = "logger"
```
The `syslogd/run` file is expected to be found in "${S}/syslogd.run"

## Additional commands

### rc-recompile

After manually modifying the `/etc/s6-rc/tree` the rc-recompile script
will do the `s6-rc-compile, s6-rc-update, symlink` magic
as proposed in [Managing compiled databases](https://skarnet.org/software/s6-rc/faq.html#compiledmanagement)
just that `rc-recompile` toggles between "compiledA" and "compiledB"
