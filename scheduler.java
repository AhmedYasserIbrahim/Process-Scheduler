import java.util.*;

public class test {
    public static void main(String[] args) {
        Process[] processes = getprocesses(); // Assuming you have a method to create the processes
        double[] waiting = new double[6];

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select the scheduling algorithm you want to see the results for:");
        System.out.println("1. FCFS");
        System.out.println("2. Non Preemptive SJF");
        System.out.println("3. Preemptive SJF");
        System.out.println("4. Non Preemptive Priority");
        System.out.println("5. Preemptive Priority");
        System.out.println("6. Round Robin");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                waiting[0] = FCFS(processes);
                System.out.println(">>>The average waiting time for FCFS is " + String.format("%.2f", waiting[0]));
                break;
            case 2:
                waiting[1] = nonPreemptiveSJF(processes);
                System.out.println(">>>The average waiting time for Non Preemptive SJF is " + String.format("%.2f", waiting[1]));
                break;
            case 3:
                waiting[2] = preemptiveSJF(processes);
                System.out.println(">>>The average waiting time for Preemptive SJF is " + String.format("%.2f", waiting[2]));
                break;
            case 4:
                waiting[3] = nonPreemptivePriority(processes);
                System.out.println(">>>The average waiting time for Non Preemptive Priority is " + String.format("%.2f", waiting[3]));
                break;
            case 5:
                waiting[4] = preemptivePriority(processes);
                System.out.println(">>>The average waiting time for Preemptive Priority is " + String.format("%.2f", waiting[4]));
                break;
            case 6:
                waiting[5] = roundRobin(processes);
                System.out.println(">>>The average waiting time for Round Robin is " + String.format("%.2f", waiting[5]));
                break;
            default:
                System.out.println(">>>Invalid choice. Exiting...");
                return;
        }

        scanner.close();
    }

    public static Process[] getprocesses() {
        Scanner s = new Scanner(System.in);
        System.out.println(">>>Enter the number of processes:");
        int num = s.nextInt();
        Process[] processes = new Process[num];
        for (int i = 0; i < processes.length; i++) {
            System.out.println(">>>Enter the arrival time, burst length, and priority for process " + i);
            Process p = new Process(i + "", s.nextInt(), s.nextInt(), s.nextInt());
            processes[i] = p;
        }

        return processes;
    }

    public static double FCFS(Process[] processes) { //Completed and Tested
        Arrays.sort(processes, Comparator.comparingInt(Process -> Process.arrivalTime));
        int currentTime = 0;
        double waitingTime = 0;
        for (int i = 0; i < processes.length; i++) {
            if(currentTime<processes[i].getArrivalTime()) {
                currentTime = processes[i].getArrivalTime();
            }
            waitingTime += currentTime - processes[i].arrivalTime;
            currentTime += processes[i].burstTime;
            if ((i + 1) < processes.length) { // To avoid index out of bound exceptions for the code
                if (processes[i + 1].arrivalTime > currentTime) { // Accounts for the gap between the termination of a
                                                                  // process and the arrival of the next
                    currentTime = processes[i + 1].arrivalTime;
                }
            }
        }

        double avgWaitingTime = waitingTime / processes.length;
        return avgWaitingTime;


    }

    public static double roundRobin(Process[] processes) { //Completed and Tested
        ArrayList<Process> remaining = new ArrayList<>(Arrays.asList(processes));
        int[] burstTimes = new int[remaining.size()];
        for (int f = 0; f < remaining.size(); f++) {
            burstTimes[f] = remaining.get(f).getBurstTime(); // Create an array of the remaining burst time for every
                                                             // process
        }
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the length of the time quantum:");
        double quantum = in.nextDouble();
        Arrays.sort(processes, Comparator.comparingInt(process -> process.getArrivalTime())); // Sort by arrival time
        int i = 0;
        int[] waiting = new int[processes.length];
        boolean terminates = false;
        int run = 0;
        int time = 0; //Keep track of the current time
        boolean arrived = true;
        while (!remaining.isEmpty()) {
            run = 0;
            terminates = false;
            arrived = true;
            if (quantum >= burstTimes[i]) { // Check if the process terminates this time quantum or not
                run = burstTimes[i];
                burstTimes[i] -= burstTimes[i];
                remaining.remove(remaining.get(i)); // If the process terminates then set its remaining burst time to 0
                                                    // and remove it
                terminates = true;
            } else {
                burstTimes[i] -= quantum; // If not, only reduce the burst time by one time quantum
            }
            for (int j = 0; j < remaining.size(); j++) { // The problem is that we calculate the waiting time for j before its arrival
                if(!(processes[j].arrivalTime>time)) { 
                    arrived = false;
                }
                if (i == j) { // If the process is itself running then do not count it as waiting time for the
                              // process
                    continue;
                } else {
                    if (terminates) {
                        if(arrived) { //Only calculate the waiting time of the process if it arrived
                            waiting[j] += run; // Only increase the waiting time for the time process i ran
                        }
                        time +=run;
                    } else {
                        if(arrived) {
                            waiting[j] += quantum; 
                        }
                        time+=quantum;
                    }
                }
            }
            if (remaining.size() != 0) { // To avoid modulos 0 exceptions
                if(!(processes[(i+1)%remaining.size()].arrivalTime>time)) { //Make sure that the next process arrives after the time quantum
                    i = (i + 1) % remaining.size(); // Go to the next process and account for circularity
                }
            }
        }
        double sum = 0;
        for (int k = 0; k < waiting.length; k++) {
            sum += waiting[k];
        }
        return sum / processes.length;
    }
    public static double nonPreemptiveSJF(Process[] processes) { //Completed and tested
        Arrays.sort(processes, Comparator.comparingInt(Process::getBurstTime));
        double elapsed = 0;
        int terminated =0;
        double waiting =0;
        ArrayList<Integer> done = new ArrayList<Integer>();
        boolean ready = false;
    
        while(terminated<processes.length) { //Loop until all processes terminate
            for(int i=0; i<processes.length; i++) { //Loop to check which processes arrived
                if((processes[i].getArrivalTime() <= elapsed) && !done.contains(i)) { //If the process arrived then execute it otherwise go to the next
                    ready = true; //Set the value of ready to true if any process arrives
                    waiting+=(elapsed-processes[i].getArrivalTime()); //Increment the waiting time
                    elapsed+=processes[i].getBurstTime(); //Increment the elapsed time by the process's burst time
                    terminated++; //Increment the number of terminated processes
                    done.add(i);
                    if(terminated==processes.length) {
                        break;
                    }
                }
                if(i==processes.length-1 && ready == false) { //If no processes are ready to be executed then increment the elapsed time and iterate again
                    elapsed++; //Set it to the next arrival time for better efficiency
                    i=-1; //Because it will be incremented immediately in the loop to reach 0
                }
            }
        } //The main problem here is to account for gaps in elapsed time and arrival time
    
        return waiting/(processes.length);
    }

    public static double preemptiveSJF(Process[] processes) {
        Process[] processes2 = Arrays.copyOf(processes, processes.length); // Duplicated the original array without re-sorting
        Arrays.sort(processes2, Comparator.comparingInt(Process::getBurstTime)); // Sorting needs to be based on the remaining time
        double elapsed = 0;
        double waiting = 0;
        int beingExecuted = -1; // Checks whether the same process is still running or it was preempted (important in waiting time)
        ArrayList<Process> done = new ArrayList<Process>(); // Keeps track of terminated processes
    
        while (done.size() < processes.length) {
            Arrays.sort(processes2, Comparator.comparingInt(Process::getBurstTime)); // Sort after every time the burst time was modified
            Process nextProcess = null;
            for (int i = 0; i < processes2.length; i++) {
                if (processes2[i].getArrivalTime() <= elapsed && !done.contains(processes2[i])) {
                    nextProcess = processes2[i];
                    break;
                }
            }
    
            if (nextProcess == null) { // None of the processes are ready yet
                elapsed++; // Increment the time and iterate again
            } else {
                if (beingExecuted == Integer.parseInt(nextProcess.getProcessID())) { // If the current process is still running, do not increment the waiting time
                    elapsed++;
                    nextProcess.setBurstTime(nextProcess.getBurstTime() - 1);
                    nextProcess.setPreviouslyExecutedTime(nextProcess.getPreviouslyExecutedTime()+1);
                    if (nextProcess.getBurstTime() == 0) { // Check if the process terminated
                        done.add(nextProcess);
                    }
                } else { // The current process is different from the previously executed process
                    waiting += elapsed - nextProcess.getArrivalTime() - nextProcess.previouslyExecutedTime;
                    nextProcess.setBurstTime(nextProcess.getBurstTime() - 1);
                    nextProcess.setPreviouslyExecutedTime(nextProcess.getPreviouslyExecutedTime()+1);
                    elapsed++;
                    if (nextProcess.getBurstTime() == 0) { // It is inside to avoid using the index -1
                        done.add(nextProcess);
                    }
                }
                beingExecuted = Integer.parseInt(nextProcess.getProcessID());
            }
        }
        return waiting / processes.length;
    }

    public static double preemptivePriority(Process[] processes) {
        Process[] processes2 = Arrays.copyOf(processes, processes.length); // Duplicated the original array without re-sorting
        Arrays.sort(processes2, Comparator.comparingInt(Process::getPriority)); // Sorting needs to be based on the remaining time
        double elapsed = 0;
        double waiting = 0;
        int beingExecuted = -1; // Checks whether the same process is still running or it was preempted (important in waiting time)
        ArrayList<Process> done = new ArrayList<Process>(); // Keeps track of terminated processes
    
        while (done.size() < processes.length) {
            //Arrays.sort(processes2, Comparator.comparingInt(Process::getBurstTime)); // Sort after every time the burst time was modified
            Process nextProcess = null;
            for (int i = 0; i < processes2.length; i++) {
                if (processes2[i].getArrivalTime() <= elapsed && !done.contains(processes2[i])) {
                    nextProcess = processes2[i];
                    break;
                }
            }
    
            if (nextProcess == null) { // None of the processes are ready yet
                elapsed++; // Increment the time and iterate again
            } else {
                if (beingExecuted == Integer.parseInt(nextProcess.getProcessID())) { // If the current process is still running, do not increment the waiting time
                    elapsed++;
                    nextProcess.setBurstTime(nextProcess.getBurstTime() - 1);
                    nextProcess.setPreviouslyExecutedTime(nextProcess.getPreviouslyExecutedTime()+1);
                    if (nextProcess.getBurstTime() == 0) { // Check if the process terminated
                        done.add(nextProcess);
                    }
                } else { // The current process is different from the previously executed process
                    waiting += elapsed - nextProcess.getArrivalTime() - nextProcess.previouslyExecutedTime;
                    nextProcess.setBurstTime(nextProcess.getBurstTime() - 1);
                    nextProcess.setPreviouslyExecutedTime(nextProcess.getPreviouslyExecutedTime()+1);
                    elapsed++;
                    if (nextProcess.getBurstTime() == 0) { // It is inside to avoid using the index -1
                        done.add(nextProcess);
                    }
                }
                beingExecuted = Integer.parseInt(nextProcess.getProcessID());
            }
        }
        return waiting / processes.length;
    }
    
    public static double nonPreemptivePriority(Process[] processes) { //Completed and tested
        Arrays.sort(processes, Comparator.comparingInt(Process::getPriority));
        double elapsed = 0;
        int terminated =0;
        double waiting =0;
        ArrayList<Integer> done = new ArrayList<Integer>();
        boolean ready = false;
    
        while(done.size()<processes.length) { //Loop until all processes terminate
            for(int i=0; i<processes.length; i++) { //Loop to check which processes arrived
                if((processes[i].getArrivalTime() <= elapsed) && !done.contains(i)) { //If the process arrived then execute it otherwise go to the next
                    ready = true; //Set the value of ready to true if any process arrives
                    waiting+=(elapsed-processes[i].getArrivalTime()); //Increment the waiting time
                    elapsed+=processes[i].getBurstTime(); //Increment the elapsed time by the process's burst time
                    terminated++; //Increment the number of terminated processes
                    done.add(i);
                    if(terminated==processes.length) {
                        break;
                    }
                }
                if(i==processes.length-1 && ready == false) { //If no processes are ready to be executed then increment the elapsed time and iterate again
                    elapsed++; //Set it to the next arrival time for better efficiency
                    i=-1; //Because it will be incremented immediately in the loop to reach 0
                }
            }
        } //The main problem here is to account for gaps in elapsed time and arrival time
    
        return waiting/(processes.length);
    }
    

}