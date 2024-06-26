class Process {
    public String processID;
    public int arrivalTime;
    public int burstTime;
    public int priority;
    public int previouslyExecutedTime; //Useful in preemption

    public Process(String processID, int arrivalTime, int burstTime, int priority) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.previouslyExecutedTime = 0;
    }

    public Process() {

    }

    public String getProcessID() {
        return processID;
    }

    public void setProcessID(String processID) {
        this.processID = processID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPreviouslyExecutedTime() {
        return previouslyExecutedTime;
    }

    public void setPreviouslyExecutedTime(int previouslyExecutedTime) {
        this.previouslyExecutedTime = previouslyExecutedTime;
    }

}