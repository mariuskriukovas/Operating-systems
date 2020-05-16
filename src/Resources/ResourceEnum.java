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
    }

    public enum Type {
        STATIC,
        DYNAMIC
    }
}

