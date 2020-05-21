package Resources;

public class ResourceEnum {

    public enum Name {
        INTERNAL_MEMORY,
        EXTERNAL_MEMORY,

        OS_END,
        USER_INPUT,
        SUPERVISOR_MEMORY,
        UPLOAD_VIRTUAL_MACHINE,
        TASK_IN_SUPERVISOR_MEMORY,
        TASK_COMPLETED,
        TASK_PARAMETERS_IN_SUPERVISOR_MEMORY,
        SWAPPING,
        PROCESS_INTERRUPT,
        PRINTLINE,
        LOADING_PACKAGE,
        TASK_IN_DRUM,
        START_EXECUTION,
        FROM_LOADER,
        FROM_PRINTLINE,
        FROM_INTERUPT,
        EXTERNAL_MEMORY_DISENGAGED,
        FROM_SWAPING,
        WAIT_UNTIL_DESTRUCTION,
        }

    public enum Type {
        STATIC,
        DYNAMIC
    }
}

