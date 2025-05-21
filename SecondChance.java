import java.util.Scanner;

public class SecondChance {

     private static int CLOCK(final Memory frames, final Integer[] pageReferences) {
        int pageFaults = 0;
        int pointer = 0;  // Clock hand (points to the next frame to consider for replacement)
        
        // Array to track reference bits for each frame
        boolean[] referenceBits = new boolean[frames.size()];
        
        for (int pageNumber : pageReferences) {
            // Check if the page is already in memory
            if (!frames.contains(pageNumber)) {

                // Page fault occurred
                pageFaults++;
                
                // Check if there's an empty frame
                boolean emptyFrameFound = false;
                for (int j = 0; j < frames.size(); j++) {
                    if (frames.isEmpty(j)) {
                        frames.put(j, pageNumber);
                        referenceBits[j] = true;  // Set reference bit for new page
                        emptyFrameFound = true;
                        pointer = (j + 1) % frames.size();  // Move hand to next frame
                        break;
                    }
                }
                
                // If no empty frame, use clock algorithm
                if (!emptyFrameFound) {
                    boolean victimFound = false;
                    // Keep looking for a victim page
                    while (!victimFound) {
                        // If reference bit is 0, replace the page
                        if (!referenceBits[pointer]) {
                            frames.put(pointer, pageNumber);
                            referenceBits[pointer] = true;  // Set reference bit for new page
                            pointer = (pointer + 1) % frames.size();  // Move hand to next frame
                            victimFound = true;
                        } else {
                            // Reference bit is 1, give second chance: reset reference bit and move clock hand
                            referenceBits[pointer] = false;
                            pointer = (pointer + 1) % frames.size();
                        }
                    }
                }
                
                System.out.println(pageNumber + ": " + frames);
            } else {
                // Page already in memory, set its reference bit
                referenceBits[frames.indexOf(pageNumber)] = true;
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

        System.out.printf("Page faults: %d.\n", CLOCK(new Memory(numFrames), toArray(referenceString)));
    }

    private static Integer[] toArray(final String referenceString) {
        final Integer[] result = new Integer[referenceString.length()];
        
        for(int i=0; i < referenceString.length(); i++) {
            result[i] = Character.digit(referenceString.charAt(i), 10);
        }
        return result;
    }
}

    

