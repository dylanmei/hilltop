# hilltop

Command-line utility for AnthillPro, a deploy, test, and release automation framework.

## setup

* JVM 1.6+
* Groovy 2.1+

    `brew/chocolatey install groovy`

* Gradle 1.6+

    `brew/chocolatey install gradle`

* Unzip the Anthill3 Dev Kit into the *./depends* folder
* Build the dependencies jar `gradle assemble`
* Run the tests `gradle test`
* Add configuration variables
    * anthill server `./hilltop config --set anthill.api_server=anthill.local`
    * authorization token `./hilltop config --set anthill.api_token=mytoken`

## commands

Config

    ./hilltop config --set anthill.api_server=anthill.local
    ./hilltop config --get anthill.api_server

Projects

    ./hilltop projects list
    ./hilltop projects list --folder Services
    ./hilltop projects show myproject
    ./hilltop projects open myproject

## contributing

1. Fork it
2. Create your feature branch `git checkout -b my-new-feature`
3. Commit your changes `git commit -am 'Added some feature'`
4. Push to the branch `git push origin my-new-feature`
5. Create new Pull Request
