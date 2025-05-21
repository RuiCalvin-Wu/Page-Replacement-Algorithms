import java.util.Scanner;

public class FirstInFirstOut {


    private static int FIFO(final Memory frames, final Integer[] pageReferences) {
        int pageFaults = 0;

        int nextToReplace = 0;  // index of the next frame to replace (FIFO alg)

        for (int pageNumber: pageReferences) {
            // check if the page is already in memory
            if (!frames.contains(pageNumber)) {
                // we know page number is not in frame, then we can add
                pageFaults++;   // we add onto pageFaults

                // replace the next frame with this pag
                frames.put(nextToReplace, pageNumber);
                nextToReplace = (nextToReplace + 1) % frames.size();

                System.out.println(pageNumber + ": " + frames);
            }
            else {
                // page number is already in frame = no page fault
                System.out.println(pageNumber + ": -");
            }
        }


        return pageFaults;
    }
    


    public static void main(final String[] args) {
        final Scanner stdIn = new Scanner(System.in);

        System.out.println("Enter the physical memory size (number of frames):");
        final int numFrames = stdIn.nextInt();
        stdIn.nextLine();

        System.out.println("Enter the string of page references:");
        final String referenceString = stdIn.nextLine();

        System.out.printf("Page faults: %d.\n", FIFO(new Memory(numFrames), toArray(referenceString)));
    }

    private static Integer[] toArray(final String referenceString) {
        final Integer[] result = new Integer[referenceString.length()];
        
        for(int i=0; i < referenceString.length(); i++) {
            result[i] = Character.digit(referenceString.charAt(i), 10);
        }
        return result;
    }
}
