import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LeastRecentlyUsed {


    private static int LRU(final Memory frames, final Integer[] pageReferences) {
        int pageFaults = 0;
        
        // Track when each page was last accessed (the index in pageReferences)
        Map<Integer, Integer> lastUsed = new HashMap<>();
        
        for (int i = 0; i < pageReferences.length; i++) {
            int pageNumber = pageReferences[i];
            
            // Check if the page is not in memory
            if (!frames.contains(pageNumber)) {
                // Page fault occurred
                pageFaults++;
                
                // Check if there's an empty frame
                boolean emptyFrameFound = false;
                for (int j = 0; j < frames.size(); j++) {
                    if (frames.isEmpty(j)) {
                        frames.put(j, pageNumber);
                        emptyFrameFound = true;
                        break;
                    }
                }
                
                // If no empty frame, find the least recently used page
                if (!emptyFrameFound) {
                    int lruFrame = 0;
                    int lruPage = frames.get(lruFrame);
                    int lruTime = lastUsed.getOrDefault(lruPage, -1);
                    
                    // Find the page with the oldest last used time
                    for (int j = 1; j < frames.size(); j++) {
                        int currentPage = frames.get(j);
                        int currentTime = lastUsed.getOrDefault(currentPage, -1);
                        
                        if (currentTime < lruTime) {
                            lruTime = currentTime;
                            lruPage = currentPage;
                            lruFrame = j;
                        }
                    }
                    
                    // Replace the least recently used page
                    frames.put(lruFrame, pageNumber);
                }
                
                System.out.println(pageNumber + ": " + frames);
            } else {
                // Page already in memory
                System.out.println(pageNumber + ": -");
            }
            
            // Update the last used time for this page
            lastUsed.put(pageNumber, i);
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

        System.out.printf("Page faults: %d.\n", LRU(new Memory(numFrames), toArray(referenceString)));
    }

    private static Integer[] toArray(final String referenceString) {
        final Integer[] result = new Integer[referenceString.length()];
        
        for(int i=0; i < referenceString.length(); i++) {
            result[i] = Character.digit(referenceString.charAt(i), 10);
        }
        return result;
    }
}

    

