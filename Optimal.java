import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Optimal {

    private static int OPT(final Memory frames, final Integer[] pageReferences) {
    int pageFaults = 0;
    
    
    // Track when each frame received its current page (for FIFO tie-breaking)
    int[] loadTime = new int[frames.size()];    // For FIFO at the end
    int time = 0;

    for (int currentIndex = 0; currentIndex < pageReferences.length; currentIndex++) {
        int pageNumber = pageReferences[currentIndex];
        
        // Check if the page is already in memory
        if (!frames.contains(pageNumber)) {
            // Page fault occurred
            pageFaults++;
            
            // loop each frame and check if each is empty. Yes, then put in
            boolean emptyFrameFound = false;
            for (int j = 0; j < frames.size(); j++) {
                if (frames.isEmpty(j)) {
                    frames.put(j, pageNumber);
                    emptyFrameFound = true;
                    break;
                }
            }
            
            // when no empty frames, then find optimal page in frame to replace
            if (!emptyFrameFound) {
                // looking ahead
                // Array to store the next used index for each page in memory
                int[] nextUse = new int[frames.size()];
                Arrays.fill(nextUse, Integer.MAX_VALUE);
                
                // Calculate the next use index for each page in memory
                for (int j = 0; j < frames.size(); j++) {
                    // loop for frames
                    int currentPage = frames.get(j);
                    for (int k = currentIndex + 1; k < pageReferences.length; k++) {
                        // loop for page refs
                        if (pageReferences[k] == currentPage) {
                            nextUse[j] = k;
                            break;
                        }
                    }
                }
                
                // replace the page that won't be used the longest time in the future or never used again.
                // Find the frames with the furthest next use
                int maxNextUse = nextUse[0];            // treats first frame as the current max
                List<Integer> candidateFrames = new ArrayList<>();      // to store the frame indices
                candidateFrames.add(0);                             // add the first frame (index 0) as the initial candidate

                // examines each remaining frame (starting from index 1).
                for (int j = 1; j < frames.size(); j++) {
                    // change current maximum if next frame is greater than current max
                    if (nextUse[j] > maxNextUse) {
                        maxNextUse = nextUse[j];
                        candidateFrames.clear();        // clear the list again
                        candidateFrames.add(j);         // add this frame as the new best candidate
                    } else if (nextUse[j] == maxNextUse) {      // if same, add another candidate
                        candidateFrames.add(j);
                    }
                    // the result is a list of candidates frames that have the furthest next use.
                }
                
                // If there are multiple candidates, use FIFO as tie-breaker
                int victimFrame;
                if (candidateFrames.size() > 1) {
                    // Find the oldest frame among candidates (FIFO)
                    victimFrame = candidateFrames.get(0);
                    
                    for (int i = 1; i < candidateFrames.size(); i++) {
                        int frame = candidateFrames.get(i);
                        
                        if (loadTime[frame] < loadTime[victimFrame]) { // Lower time means loaded earlier
                            victimFrame = frame;
                        }
                    }
                } else {
                    victimFrame = candidateFrames.get(0);
                }
                
                // Replace the page that won't be used for the longest time
                frames.put(victimFrame, pageNumber);
                loadTime[victimFrame] = time++; // Update the load time of this frame
            }
            
            System.out.println(pageNumber + ": " + frames);
        } else {
            // Page already in memory
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

        System.out.printf("Page faults: %d.\n", OPT(new Memory(numFrames), toArray(referenceString)));
    }

    private static Integer[] toArray(final String referenceString) {
        final Integer[] result = new Integer[referenceString.length()];
        
        for(int i=0; i < referenceString.length(); i++) {
            result[i] = Character.digit(referenceString.charAt(i), 10);
        }
        return result;
    }
}