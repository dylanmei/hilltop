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
* Add configuration variables
    * anthill server `ht config set anthill.api_server=anthill.local`
    * authorization token `ht config set anthill.api_token=mytoken`

## commands

Config

    ./hilltop config show
    ./hilltop config set <name=value>
    ./hilltop config get <name>

Projects

    ./hilltop project list
    ./hilltop project list --folder <folder>
    ./hilltop project show <project or .>
    ./hilltop project open <project or .>
    ./hilltop project remove <project or .>

Workflows

    ./hilltop workflow list <project or .>
    ./hilltop workflow show <project or .> <workflow>
    ./hilltop workflow open <project or .> <workflow>
    ./hilltop workflow remove <project or .> <workflow>

Folders

    ./hilltop folder list
    ./hilltop folder show <name or path>

Builds

    ./hilltop build show <buildlife>
    ./hilltop build open <buildlife>
    ./hilltop build new <project or .> <workflow>
    ./hilltop build run <buildlife> <workflow> <environment>
    ./hilltop build remove <buildlife>

Build requests

    ./hilltop request open <request>
    ./hilltop request show <request>

Environments

    ./hilltop environment list
    ./hilltop environment list --group <group>
    ./hilltop environment show <environment>
    ./hilltop environment open <environment>

Agents

    ./hilltop agent list
    ./hilltop agent show <agent>
    ./hilltop agent open <agent>

Lifecycles

    ./hilltop lifecycle list
    ./hilltop lifecycle show <lifecycle>
    ./hilltop lifecycle open <lifecycle>

## contributing

Hilltop uses the Gradle project automation tool. Gradle tasks are discoverable with `gradle tasks`. The *./hilltop* shell-script is included as a shortcut for `gradle run` *+ args*.

1. Fork it
2. Create your feature branch `git checkout -b my-new-feature`
3. Commit your changes `git commit -am 'Added some feature'`
4. Push to the branch `git push origin my-new-feature`
5. Create new Pull Request
