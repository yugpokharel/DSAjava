import java.util.concurrent.Semaphore;

class NumberPrinter {
    public void printZero() {
        System.out.print("0");
    }

    public void printEven(int number) {
        System.out.print(number);
    }

    public void printOdd(int number) {
        System.out.print(number);
    }
}

// ThreadController class to synchronize three threads
class ThreadController {
    private int n;
    private NumberPrinter printer;
    
    private Semaphore zeroSemaphore = new Semaphore(1);
    private Semaphore evenSemaphore = new Semaphore(0);
    private Semaphore oddSemaphore = new Semaphore(0);

    public ThreadController(int n, NumberPrinter printer) {
        this.n = n;
        this.printer = printer;
    }

    public void printZero() {
        try {
            for (int i = 1; i <= n; i++) {
                zeroSemaphore.acquire(); // Wait for turn
                printer.printZero();
                if (i % 2 == 0) {
                    evenSemaphore.release(); // Allow even numbers
                } else {
                    oddSemaphore.release();  // Allow odd numbers
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void printEven() {
        try {
            for (int i = 2; i <= n; i += 2) {
                evenSemaphore.acquire();
                printer.printEven(i);
                zeroSemaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void printOdd() {
        try {
            for (int i = 1; i <= n; i += 2) {
                oddSemaphore.acquire();
                printer.printOdd(i);
                zeroSemaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Main class to run the threads
public class NumberPrinterThreads {
    public static void main(String[] args) {
        int n = 5;  // Change n as needed
        NumberPrinter printer = new NumberPrinter();
        ThreadController controller = new ThreadController(n, printer);

        Thread zeroThread = new Thread(controller::printZero);
        Thread evenThread = new Thread(controller::printEven);
        Thread oddThread = new Thread(controller::printOdd);

        zeroThread.start();
        evenThread.start();
        oddThread.start();

        try {
            zeroThread.join();
            evenThread.join();
            oddThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
