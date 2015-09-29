# hilltop

Command-line utility for AnthillPro, a deploy, test, and release automation framework.

## setup

* JRE 1.6+
* Groovy 2.1+

    `brew/chocolatey install groovy`

* Gradle 1.6+

    `brew/chocolatey install gradle`
* Clone this repo `git clone https://github.com/dylanmei/hilltop && cd hilltop`
* Unzip the [Anthill3 Dev Kit](http://docs.urbancode.com/anthill3-help-3.8/html/DevKit.html) into the *./depends* folder.
 * Verify the path *./depends/anthill3-dev-kit/remoting* exists.
* Build the hilltop app `gradle installApp`
* Create an alias to hilltop `alias ht=$(pwd)/build/install/hilltop/bin/hilltop`
    * *optional for bash users:* dot source auto-completion script, e.g.
    
     `. /c/your-src-location-here/hilltop/src/shell-integrations/bash/tab-completion.sh`
* Add configuration variables
    * Anthill server: `ht config set anthill.api_server=anthill.local`
    * Authorization token: `ht config set anthill.api_token=mytoken`
    * Your configuration is saved to the file `~/.hilltop`

## commands

Config

    ./hilltop config get <name>
    ./hilltop config set <name=value> ...
    ./hilltop config remove <name>
    ./hilltop config show

Projects

    ./hilltop project list
    ./hilltop project list --folder <folder-name>
    ./hilltop project find <project-name>
    ./hilltop project show <project-name>
    ./hilltop project open <project-name>
    ./hilltop project remove <project-name>
    ./hilltop project copy <project-name> <new-name>

Workflows

    ./hilltop workflow list <project-name>
    ./hilltop workflow show <project-name> <workflow-name>
    ./hilltop workflow open <project-name> <workflow-name>
    ./hilltop workflow remove <project-name> <workflow-name>
    ./hilltop workflow copy <project-name> <workflow-name> <new-name>
    ./hilltop workflow exec <operational-workflow-name> <environment-name> <property-name=property-value> ...

Workflow Properties

    ./hilltop workflow-property list <project-name> <workflow-name>
    ./hilltop workflow-property add <project-name> <workflow-name> <propertyName> <propertyValue>
    ./hilltop workflow-property remove <project-name> <workflow-name> <propertyName>
    ./hilltop workflow-property set <project-name> <workflow-name> <propertyName> <propertyValue>

Workflow Dependencies

    ./hilltop workflow-dependency list <project-name> <workflow-name>
    ./hilltop workflow-dependency add <dependent-project-name> <dependent-workflow-name> <dependency-project-name> <dependency-workflow-name> <artifact> <location>
    ./hilltop workflow-dependency remove <dependent-project-name> <dependent-workflow-name> <dependency-project-name> <dependency-workflow-name>
    ./hilltop workflow-dependency conflict-strategy <project-name> <workflow-name> <conflict-strategy> (fail/old/new)

Source Config

    ./hilltop source-config set <project-name> <workflow-name> <source-type> <name=value> ...
    NOTE: Currently, only 'git' source type is supported with properties: [branch, remote-url]

Folders

    ./hilltop folder list
    ./hilltop folder show <name or path>

Builds

    ./hilltop build new <project-name> <workflow-name> <property-name=property-value> ...
    ./hilltop build new --properties-file <file-path> <project-name> <workflow-name>
    ./hilltop build run <buildlife> <workflow-name> <environment-name> <property-name=property-value> ...
    ./hilltop build show <buildlife>
    ./hilltop build latest <project-name> <workflow-name>
    ./hilltop build latest <project-name> <workflow-name> <status-name>
    ./hilltop build open <buildlife>
    ./hilltop build remove <buildlife>

Build Links

    ./hilltop build-link list <buildlife>
    ./hilltop build-link add <buildlife> <link-name> <link-url>
    ./hilltop build-link remove <buildlife> <link-name>
    ./hilltop build-link open <buildlife> <link-name>


Build Requests

    ./hilltop build-request show <request>
    ./hilltop build-request open <request>
    ./hilltop build-request recent <project-name>

Environments

    ./hilltop environment list
    ./hilltop environment list --group <group-name>
    ./hilltop environment show <environment-name>
    ./hilltop environment open <environment-name>

Agents

    ./hilltop agent list
    ./hilltop agent show <agent-name>
    ./hilltop agent open <agent-name>

Lifecycles

    ./hilltop lifecycle list
    ./hilltop lifecycle show <lifecycle-name>
    ./hilltop lifecycle open <lifecycle-name>

## notes

* **.** can be used in place of &lt;*project-name&gt; to infer based on the current working directory, e.g. `workflow list .`

* `--json` can be used on query commands to output to console as JSON rather than free-form text

## contributing

Hilltop uses the Gradle project automation tool. Gradle tasks are discoverable with `gradle tasks`. The *./hilltop* shell-script is included as a shortcut for `gradle run` *+ args*.

1. Fork it
2. Create your feature branch `git checkout -b my-new-feature`
3. Commit your changes `git commit -am 'Added some feature'`
4. Push to the branch `git push origin my-new-feature`
5. Create new Pull Request
