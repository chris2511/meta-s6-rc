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

## Documentation

The S6 system itself is thoroughly documented by its author.

This layer adds the `s6rc.bbclass`, which allows to easily
create new services with dependencies. It populates the
`/etc/s6-rc/tree` directory with services declared by the following
variables:
 - S6RC\_BUNDLES, S6RC\_BUNDLE\_% and S6RC\_BUNDLE\_%[ ]
 - S6RC\_ONESHOTS and S6RC\_ONESHOT\_%[ ]
 - S6RC\_LONGRUNS and S6RC\_LONGRUN\_%[ ] S6RC\_LONGRUN\_%\_log[ ]
 - S6RC\_INITD\_SYMLINKS

`s6-init.bb` shows how to use them in practice.
Please read/know about the concepts documented in
[s6-rc-compile](https://skarnet.org/software/s6-rc/s6-rc-compile.html)

The S6RC\_INITD\_SYMLINKS variable declares a space separated
list of service names that will be put as symlinks to `s6-startstop`
to `/etc/init.d/`. This enables SysV/Systemd feeling by providing:
`/etc/init.d/<service> [start|stop|restart|enable|disable|status|pid|
crashes|logs|logf]`

### Bundles

`S6RC_BUNDLES` contains a space separated list of bundle-names.
Each `S6RC_BUNDLE_%` with % replaced by a bundle name contains a space separated
list of services and bundles that will be written to the
`contents` file of the bundle.

Example:
```
S6RC_BUNDLES = "bundleA bundleB"
S6RC_BUNDLE_bundleA = "service1 service2"
S6RC_BUNDLE_bundleB = "svcx svcy bundleA"
```
will result in
```
/etc/s6-rc/tree/bundleA/type (which contains 'bundle')
/etc/s6-rc/tree/bundleA/contents (which contains 'service1\nservice2')
/etc/s6-rc/tree/bundleB/type (which contains 'bundle')
/etc/s6-rc/tree/bundleB/contents (which contains 'svx\nsvy\nbundleA')
```

Adding bundles, oneshots and longruns to bundles defined in other recipes,
for example the `default` bundle, can be achieved by the
`S6RC_[LONGRUN|ONESHOT|BUNDLE]_<name>[bundles]` variable.
Adding the longrun `dropbear` to the `default` and `network` bundle
is done by
```
S6RC_LONGRUN_dropbear[bundles] = "default networking"
```
Non-existing bundles will be created on-the-fly.

### Atomic services

All regular files recognized by s6-rc-compile can either be directly
added as `S6RC_ONESHOT_runner-a[dependencies] = "runner-b bundle-x"`
or provided as `<service-name>.<property>` file.
In this example a `runner-a.dependencies` will be used as runner-a/dependencies file verbatim.
This is especially useful for `up` and `run` files, which may become lengthy
and cumbersome to write in one line.

All atomic services may additionally contain the special file `influences`
which is the inverse of `dependencies`:
All atomics listed in `influences` of an other atomic
will depend on the other atomic.

### Oneshots

`S6RC_ONESHOTS` contains a space separated list of oneshot-names.
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
/etc/s6-rc/tree/start/type (which contains 'oneshot')
/etc/s6-rc/tree/start/up (which contains 'echo "init-stage2 starting."')
/etc/s6-rc/tree/mount-procsysdev/type (which contains 'oneshot')
/etc/s6-rc/tree/mount-procsysdev/dependencies (which contains 'start')
```
An existing file `${S}/mount-procsysdev.up` will be copied verbatim to
`/etc/s6-rc/tree/mount-procsysdev/up`

### Longruns

`S6RC_LONGRUNS` contains a space separated list of longrun-names.
`S6RC_LONGRUN_%[ ]` declares properties with % replaced by a longrun-name

The functionality and behavior is analog to the oneshots, just with the
longrun properties.

A log service will always be setup, unless `S6RC_LONGRUN_%[no-log]`
exists. `S6RC_LONGRUN_%_log[]` properties are evaluated
with the following property files:
 - name: `<longrun-name>-log`
 - notification-fd: 3
 - consumer-for: `<longrun-name>`
 - dependencies: mount-temp
 - run: `umask {umask} s6-setuidgid {user} s6-log -d3 {script} {dir}`
   with {...} replaced by overrideable defaults:
   - umask: 0037
   - user: logger
   - script: `T s100000 n10`
   - dir: `/var/log/<longrun-name>`

All properties can be overwritten (and not extended) by an explicit
value. For example: `S6RC_LONGRUN_%_log[dependencies] = "mount-temp other"`

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

### Templates

`S6RC_TEMPLATES` contains a space separated list of templates that
can be instantiated by the [*rc-dynamic*](#rc-dynamic) command.
`S6RC_TEMPLATE_%[ ]` declares service properties with % replaced by a template-name.
A *run* script is mandatory

A log service will be setup if `S6RC_TEMPLATE_%_log[mode]` exists and contains a valid value. For template services, there are two log modes available "all-in-one" and "per-instance".

With `all-in-one` a single log file service will be setup that can be used to collect the log entries of all service instances. For this to work, the log service stores a file descriptor in the s6-fdholder-store that must be retrieved by each service instance.

The file descriptor can be retrieved with `s6-fdholder-retrieve /service/s6rc-fdholder/s pipe:s6rc-<template>`.

The mode `per-instance` creates a minimal log service for each service instance. Stdout will be automatically forwarded to this log service.

## Additional commands

### rc-recompile

After manually modifying the `/etc/s6-rc/tree` the rc-recompile script
will do the `s6-rc-compile, s6-rc-update, symlink` magic
as proposed in [Managing compiled databases](https://skarnet.org/software/s6-rc/faq.html#compiledmanagement)
just that `rc-recompile` toggles between "compiledA" and "compiledB"

### rc-config

usage: `rc-config [enable|disable] <serviceX|oneshotY|bundleZ>`

Adds or removes the given services, oneshots and bundles
to the `default` bundle, which is the default bundle when booting.

It first modifies the source tree in `/etc/s6-rc/tree/default/contents`,
then adds or removes it to the compiled database by
`s6-rc-bundle -f add default ...`
and finally updates the live state by `s6-rc -up -v2 change default`.

### rc-dynamic

usage `rc-dynamic [setup|teardown] <service-name>

Create an instance from an [S6RC\_TEMPLATE](#templates). The name of the dynamic service is **instance@template**
The template from the directory */etc/s6-rc/templates/* is copied with the whole service name
to */run/service*. An envfile called *env/instance* is created inside the service with 2 variables:
 - INSTANCE="\<instance-name\>"
 - TEMPLATE="\<template-name\>"

The run and finish scripts may source it or the execlie way: *envfile env/instance* it and
use the INSTANCE variable name for starting this specific instance.
An optional executable file in the template: *data/check-instance* may be used to validate
the instance name. It will be called with the instance-name as argument and must exit 0
if the instance is valid. If it exits non-zero, the instance creation fails.

### s6-startstop

The script `/etc/init.d/s6-startstop` is never called directly, but called
via a symlink, which determines the service to act on.
It provides the following subcommands:
 - *enable:* permanently enable and start the service
 - *disable:* permanently stop and disable the service
 - *start:* temporarily start the service and all its dependencies until
   reboot. After a reboot it returns to the enabled/disabled state
 - *stop:* temporarily stop the service and all its dependencies until reboot.
   After a reboot it returns to the enabled/disabled state
 - *restart:* same as start, but terminates the service
   if it is already running.
 - *status:* prints the `s6-svstat` output
 - *pid:* prints the pid of the service
 - *crashes:* prints the death tally of the service
 - *logs:* prints the complete log
 - *logf:* does a tail -f of the last logs
