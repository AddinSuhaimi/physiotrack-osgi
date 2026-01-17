# physiotrack-osgi

OSGi CLI-only implementation scaffold for PhysioTrack.

## What you get
- Maven multi-module repo
- For each module: `<module>-api` and `<module>-impl` bundles
- `app-runner` bundle that starts and prints detected services
- `progress-tracking`, `personal-info`, `user-management` etc included

## HOW TO BUILD AND RUN AFTER CHANGES

1. At repo root (.../physiotrack-osgi), run:

`mvn clean package`

2. After build success, navigate to .../physiotrack-osgi/runtime/felix/felix-framework-7.0.5, run:

`java -jar bin/felix.jar`

you will see `g!` indicating gogo has started

3. Run `id` to check your module api and implementations id.

for example, appointment-api is 7 and appointment-impl is 15, so after changes to appointment module has been made, run:

`stop 7`

`update 7`

`start 7`

`stop 15`

`update 15`

`start 15`

4. If you have made changes to app-runner Activator.java. run the following commands in gogo:

`stop 23`

`update 23`

`start 23`

