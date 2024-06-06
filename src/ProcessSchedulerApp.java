import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProcessSchedulerApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WelcomeScreenUI().setVisible(true);
        });
    }
}

class WelcomeScreenUI extends JFrame {
    public WelcomeScreenUI() {
        setTitle("Welcome");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel welcomeLabel = new JLabel("Welcome to Process Scheduler");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);

        JButton startButton = new JButton("Start");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(startButton, gbc);

        startButton.addActionListener(e -> {
            dispose();
            new ProcessSchedulerUI().setVisible(true);
        });

        add(panel);
        setLocationRelativeTo(null);
    }
}

class ProcessSchedulerUI extends JFrame {
    public ProcessSchedulerUI() {
        setTitle("Process Scheduler");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("PROCESS SCHEDULER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel burstTimeLabel = new JLabel("Burst Time");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(burstTimeLabel, gbc);

        JTextField burstTimeField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(burstTimeField, gbc);

        JLabel arrivalTimeLabel = new JLabel("Arrival Time");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(arrivalTimeLabel, gbc);

        JTextField arrivalTimeField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(arrivalTimeField, gbc);

        JLabel priorityLabel = new JLabel("Priority");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(priorityLabel, gbc);

        JTextField priorityField = new JTextField();
        priorityField.setEditable(false); // Make priority field read-only
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(priorityField, gbc);

        JButton runButton = new JButton("Run");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(runButton, gbc);

        JTextArea resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(new JScrollPane(resultArea), gbc);

        // DocumentListener to update priority field based on burst time and arrival
        // time
        DocumentListener updatePriority = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateAndSetPriority();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateAndSetPriority();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateAndSetPriority();
            }

            private void calculateAndSetPriority() {
                String burstTimeText = burstTimeField.getText().trim();
                String arrivalTimeText = arrivalTimeField.getText().trim();

                if (!burstTimeText.isEmpty() && !arrivalTimeText.isEmpty()) {
                    String[] burstTimes = burstTimeText.split(",");
                    String[] arrivalTimes = arrivalTimeText.split(",");

                    if (burstTimes.length == arrivalTimes.length) {
                        try {
                            StringBuilder priorityValues = new StringBuilder();
                            for (int i = 0; i < burstTimes.length; i++) {
                                int burstTime = Integer.parseInt(burstTimes[i].trim());
                                int arrivalTime = Integer.parseInt(arrivalTimes[i].trim());
                                double priority = (burstTime + arrivalTime) / 2.0;
                                priorityValues.append(priority);
                                if (i < burstTimes.length - 1) {
                                    priorityValues.append(", ");
                                }
                            }
                            priorityField.setText(priorityValues.toString());
                        } catch (NumberFormatException ex) {
                            priorityField.setText("Invalid input");
                        }
                    } else {
                        priorityField.setText("Mismatched input lengths");
                    }
                } else {
                    priorityField.setText("");
                }
            }
        };

        burstTimeField.getDocument().addDocumentListener(updatePriority);
        arrivalTimeField.getDocument().addDocumentListener(updatePriority);

        runButton.addActionListener(e -> {
            String burstTimeString = burstTimeField.getText().trim();
            String arrivalTimeString = arrivalTimeField.getText().trim();
            String priorityString = priorityField.getText().trim();

            if (burstTimeString.isEmpty() || arrivalTimeString.isEmpty() || priorityString.isEmpty()) {
                resultArea.setText("Please enter values in all fields.");
                return;
            }

            String[] burstTimesArray = burstTimeString.split(",");
            String[] arrivalTimesArray = arrivalTimeString.split(",");
            String[] prioritiesArray = priorityString.split(",");

            if (burstTimesArray.length != arrivalTimesArray.length
                    || burstTimesArray.length != prioritiesArray.length) {
                resultArea.setText("Mismatched input lengths");
                return;
            }

            List<Process> processes = new ArrayList<>();

            for (int i = 0; i < burstTimesArray.length; i++) {
                try {
                    int burstTime = Integer.parseInt(burstTimesArray[i].trim());
                    int arrivalTime = Integer.parseInt(arrivalTimesArray[i].trim());
                    double priority = Double.parseDouble(prioritiesArray[i].trim());

                    if (burstTime < 0 || arrivalTime < 0 || priority < 0) {
                        resultArea.setText("Invalid input at index " + i + ": Please enter non-negative values.\n");
                        return;
                    } else {
                        Process process = new Process("P" + (i + 1), arrivalTime, burstTime, priority);
                        processes.add(process);
                    }
                } catch (NumberFormatException ex) {
                    resultArea.setText(
                            "Invalid input at index " + i + ": Please enter valid integer values in all fields.\n");
                    return;
                }
            }

            Scheduler scheduler = new Scheduler();
            for (Process process : processes) {
                scheduler.addProcess(process);
            }
            scheduler.run();

            StringBuilder result = new StringBuilder();
            result.append("Gantt Chart: ").append(scheduler.getGanttChart()).append("\n\n");
            result.append("Average Turnaround Time: ").append(scheduler.getAverageTurnaroundTime()).append("\n");
            result.append("Average Waiting Time: ").append(scheduler.getAverageWaitingTime());

            resultArea.setText(result.toString());
        });

        add(panel);
        setLocationRelativeTo(null);
    }
}

class Process {
    private String id;
    private int arrivalTime;
    private int burstTime;
    private double priority;
    private int startTime = -1; // Initialize to -1 to distinguish unstarted processes
    private int completionTime;
    private int remainingTime;

    public Process(String id, int arrivalTime, int burstTime, double priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }

    public String getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public double getPriority() {
        return priority;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getTurnaroundTime() {
        return completionTime - arrivalTime;
    }

    public int getWaitingTime() {
        return getTurnaroundTime() - burstTime;
    }

    public void setTurnaroundTime(int i) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setTurnaroundTime'");
    }

    public void setWaitingTime(int i) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setWaitingTime'");
    }
}

class Scheduler {
    private List<Process> processes;
    private List<GanttChartEntry> ganttChart;
    private double totalWaitingTime;
    private double totalTurnaroundTime;
    private int initialProcessCount;

    public Scheduler() {
        processes = new ArrayList<>();
        ganttChart = new ArrayList<>();
    }

    public void addProcess(Process process) {
        processes.add(process);
    }

    public void run() {
        initialProcessCount = processes.size();
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int[] remainingBurstTimes = new int[processes.size()];
        for (int i = 0; i < processes.size(); i++) {
            remainingBurstTimes[i] = processes.get(i).getBurstTime();
        }

        int currentTime = processes.stream().mapToInt(Process::getArrivalTime).min().orElse(0);
        int completed = 0;

        if (currentTime > 0) {
            ganttChart.add(new GanttChartEntry("-", currentTime));
        }

        while (completed < processes.size()) {
            int idx = -1;
            double highestPriority = Double.MAX_VALUE;

            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).getArrivalTime() <= currentTime &&
                        remainingBurstTimes[i] > 0 &&
                        processes.get(i).getPriority() < highestPriority) {
                    highestPriority = processes.get(i).getPriority();
                    idx = i;
                }
            }

            if (idx == -1) {
                currentTime++;
                if (ganttChart.get(ganttChart.size() - 1).getId().equals("-")) {
                    ganttChart.get(ganttChart.size() - 1).setEndTime(currentTime);
                } else {
                    ganttChart.add(new GanttChartEntry("-", currentTime));
                }
                continue;
            }

            if (ganttChart.isEmpty()
                    || !ganttChart.get(ganttChart.size() - 1).getId().equals(processes.get(idx).getId())) {
                ganttChart.add(new GanttChartEntry(processes.get(idx).getId(), currentTime));
            }

            if (processes.get(idx).getStartTime() == -1) {
                processes.get(idx).setStartTime(currentTime);
            }

            remainingBurstTimes[idx]--;
            currentTime++;

            if (remainingBurstTimes[idx] == 0) {
                processes.get(idx).setCompletionTime(currentTime);
                processes.get(idx).setTurnaroundTime(currentTime - processes.get(idx).getArrivalTime());
                processes.get(idx)
                        .setWaitingTime(processes.get(idx).getTurnaroundTime() - processes.get(idx).getBurstTime());
                totalWaitingTime += processes.get(idx).getWaitingTime();
                totalTurnaroundTime += processes.get(idx).getTurnaroundTime();
                completed++;
            }
        }

        // Update the end_time for each entry in the Gantt chart
        for (int i = 0; i < ganttChart.size() - 1; i++) {
            ganttChart.get(i).setEndTime(ganttChart.get(i + 1).getEndTime());
        }
        ganttChart.get(ganttChart.size() - 1).setEndTime(currentTime);
    }

    public List<GanttChartEntry> getGanttChart() {
        return ganttChart;
    }

    public double getAverageWaitingTime() {
        return totalWaitingTime / initialProcessCount;
    }

    public double getAverageTurnaroundTime() {
        return totalTurnaroundTime / initialProcessCount;
    }
}

class GanttChartEntry {
    private String id;
    private int endTime;

    public GanttChartEntry(String id, int endTime) {
        this.id = id;
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getEndTime() {
        return endTime;
    }
}
