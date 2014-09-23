_hilltop()
{
    local cur prev opts
    COMPREPLY=()
    cur="${COMP_WORDS[COMP_CWORD]}"
    prev="${COMP_WORDS[COMP_CWORD-1]}"

    opts="config project workflow workflow-dependency folder build request environment agent lifecycle" # colony disabled

    case "${prev}" in
      config)
        local config_options="get set remove show"
        COMPREPLY=($(compgen -W "${config_options}" ${cur}))
        return 0
        ;;
      project)
        local project_options="list find show open remove"
        COMPREPLY=($(compgen -W "${project_options}" ${cur}))
        return 0
        ;;
      workflow)
        local workflow_options="list show open remove"
        COMPREPLY=($(compgen -W "${workflow_options}" ${cur}))
        return 0
        ;;
      workflow-dependency)
        local workflow_dependency_options="list add remove"
        COMPREPLY=($(compgen -W "${workflow_dependency_options}" ${cur}))
        return 0
        ;;
      folder)
        local folder_options="list show"
        COMPREPLY=($(compgen -W "${folder_options}" ${cur}))
        return 0
        ;;
      build)
        local build_options="new run show open remove"
        COMPREPLY=($(compgen -W "${build_options}" ${cur}))
        return 0
        ;;
      request)
        local request_options="show open"
        COMPREPLY=($(compgen -W "${request_options}" ${cur}))
        return 0
        ;;
      environment)
        local request_options="list show open"
        COMPREPLY=($(compgen -W "${request_options}" ${cur}))
        return 0
        ;;
      agent)
        local agent_options="list show open"
        COMPREPLY=($(compgen -W "${agent_options}" ${cur}))
        return 0
        ;;
      lifecycle)
        local lifecycle_options="list show open"
        COMPREPLY=($(compgen -W "${lifecycle_options}" ${cur}))
        return 0
        ;;
#      colony)
#        local colony_options="init exec"
#        COMPREPLY=($(compgen -W "${colony_options}" ${cur}))
#        return 0
#        ;;
      *)
        COMPREPLY=($(compgen -W "${opts}" ${cur}))  
        return 0
        ;;
    esac
}

complete -F _hilltop ht