

## INSTALLATION INSTRUCTIONS


go to:

```
Intellij> settings > plugins > Install plugin from disk
```
and choose select following file (click to download):
[intellij-groovy.zip](https://github.com/machak/intelij-hippo-groovy/raw/master/intellij-groovy.zip)

## SETUP *)

go to:

Intellij> settings > Hippo Groovy editor
Default values:

```
username: admin
password: admin
groovy root dir: cms/src/main/java/groovy
rmi: rmi://localhost:1099/hipporepository

```

# USAGE

Fetch all templates:
```
File  > Load groovy scripts from repository
```
After editing (or adding a new template):

```
File >  Save groovy script to repository
```





# *) RMI

NOTE: you need to enable RMI port within conf/context.xml by adding following lines:
```
<Parameter name="start-remote-server" value="true" override="false"/>
<Parameter name="repository-address" value="rmi://127.0.0.1:1099/hipporepository" override="false"/>
```