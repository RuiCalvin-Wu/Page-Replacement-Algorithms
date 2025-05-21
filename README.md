# Page Replacement Algorithms: FIFO, LRU, OPT, and CLOCK
Rui Wu
I'll walk you through how each of these page replacement algorithms works, explaining both the conceptual approach and the code implementation step by step.

## 1. FIFO (First-In-First-Out)

### Concept
FIFO is the simplest page replacement algorithm. It treats memory frames like a queue - the first page loaded will be the first one replaced when memory is full. It doesn't consider how frequently or recently pages are used.

### Code Walkthrough

```java
private static int FIFO(final Memory frames, final Integer[] pageReferences) {
    int pageFaults = 0;
    int nextToReplace = 0;  // index of the next frame to replace (FIFO alg)

    for (int pageNumber: pageReferences) {
        // check if the page is already in memory
        if (!frames.contains(pageNumber)) {
            // we know page number is not in frame, then we can add
            pageFaults++;   // we add onto pageFaults

            // replace the next frame with this page
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
```

1. **Initialization**:
   - `pageFaults` tracks how many page faults occur (when a requested page isn't in memory)
   - `nextToReplace` tracks which frame position should be replaced next (starting at 0)

2. **For each page request**:
   - Check if the requested page is already in memory with `frames.contains(pageNumber)`
   
3. **If page is not in memory** (page fault):
   - Increment the page fault counter
   - Put the new page in the current "next to replace" position: `frames.put(nextToReplace, pageNumber)`
   - Update the next position to replace: `nextToReplace = (nextToReplace + 1) % frames.size()`
     - The modulo operation creates a circular pattern (0, 1, 2, ..., frame_size-1, 0, 1, ...)
   - Print the memory state after replacement

4. **If page is already in memory**:
   - No action needed (just print that no change occurred)

5. **Return** the total number of page faults

### Key Insight
FIFO maintains a circular pointer through the frames. It doesn't care about page usage patterns - it simply replaces pages in the order they arrived, like standing in line.

## 2. LRU (Least Recently Used)

### Concept
LRU replaces the page that hasn't been accessed for the longest time. This is based on the principle of temporal locality - pages that have been used recently are likely to be used again soon.

### Code Walkthrough

```java
private static int LRU(final Memory frames, final Integer[] pageReferences) {
    int pageFaults = 0;
    
    // Track when each page was last accessed (the index in pageReferences)
    Map<Integer, Integer> lastUsed = new HashMap<>();
    
    for (int i = 0; i < pageReferences.length; i++) {
        int pageNumber = pageReferences[i];
        
        // Check if the page is already in memory
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
```

1. **Initialization**:
   - `pageFaults` tracks page faults
   - `lastUsed` HashMap tracks when each page was last accessed (index in pageReferences = "time")

2. **For each page request** (using index i as the "timestamp"):
   - Check if the page is already in memory

3. **If page is not in memory** (page fault):
   - Increment page fault counter
   - **Try to find an empty frame first**:
     - Loop through all frames checking for empty ones
     - If found, use that frame and set `emptyFrameFound = true`
   
   - **If no empty frame** (all frames are full):
     - Initialize variables to track the LRU frame
     - Start with frame 0 as the candidate for replacement
     - For each frame in memory:
       - Get the "time" when its page was last used
       - If this time is older (smaller) than our current oldest time, update our candidate
     - Replace the page in the frame that hasn't been used for the longest time

4. **Whether there was a page fault or not**:
   - Update the "last used" time for the current page: `lastUsed.put(pageNumber, i)`
   - This is crucial! It records that we just accessed this page at the current time

5. **Return** the total number of page faults

### Key Insight
LRU maintains a history of when each page was last accessed and always replaces the "stalest" page. It adapts to the actual usage patterns but requires tracking each page access.

## 3. OPT (Optimal)

### Concept
OPT looks into the future to make the best possible replacement decision. It replaces the page that won't be needed for the longest time in the future. This is theoretically optimal but requires knowing future access patterns.

### Code Walkthrough

```java
private static int OPT(final Memory frames, final Integer[] pageReferences) {
    int pageFaults = 0;
    
    // Track when each frame received its current page (for FIFO tie-breaking)
    int[] loadTime = new int[frames.size()];
    int time = 0;

    for (int currentIndex = 0; currentIndex < pageReferences.length; currentIndex++) {
        int pageNumber = pageReferences[currentIndex];
        
        // Check if the page is already in memory
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
            
            // If no empty frame, find the optimal page to replace
            if (!emptyFrameFound) {
                // Array to store the next use index for each page in memory
                int[] nextUse = new int[frames.size()];
                Arrays.fill(nextUse, Integer.MAX_VALUE);
                
                // Calculate the next use index for each page in memory
                for (int j = 0; j < frames.size(); j++) {
                    int currentPage = frames.get(j);
                    for (int k = currentIndex + 1; k < pageReferences.length; k++) {
                        if (pageReferences[k] == currentPage) {
                            nextUse[j] = k;
                            break;
                        }
                    }
                }
                
                // Find the frames with the furthest next use
                int maxNextUse = nextUse[0];
                List<Integer> candidateFrames = new ArrayList<>();
                candidateFrames.add(0);

                for (int j = 1; j < frames.size(); j++) {
                    if (nextUse[j] > maxNextUse) {
                        maxNextUse = nextUse[j];
                        candidateFrames.clear();
                        candidateFrames.add(j);
                    } else if (nextUse[j] == maxNextUse) {
                        candidateFrames.add(j);
                    }
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
```

1. **Initialization**:
   - `pageFaults` tracks page faults
   - `loadTime` array tracks when each frame received its current page (for tiebreaking)
   - `time` variable acts as a counter for load times

2. **For each page request**:
   - Check if the page is already in memory

3. **If page is not in memory** (page fault):
   - Increment page fault counter
   - Try to find an empty frame first
   
   - **If no empty frame** (all frames are full):
     - Create `nextUse` array to store when each page in memory will be used next
     - Initially fill with `Integer.MAX_VALUE` (representing "never used again")
     
     - **Look into the future for each page in memory**:
       - For each page currently in a frame, scan forward in the reference string
       - Record the position of its next use (or leave as MAX_VALUE if never used again)
     
     - **Find the page(s) that won't be used for the longest time**:
       - Start with frame 0 as candidate
       - Compare all frames to find those with the highest "next use" index
       - Collect all frames that tie for "won't be used for longest time"
     
     - **Handle ties using FIFO** (choose the oldest loaded page):
       - If multiple candidate frames, select the one with the lowest load time
       - Otherwise, just use the single candidate
     
     - Replace the selected victim page and update its load time

4. **Return** the total number of page faults

### Key Insight
OPT provides a theoretical lower bound on page faults by using perfect knowledge of future accesses. In practice, we can't predict the future, but OPT serves as a benchmark to evaluate other algorithms.

## 4. CLOCK (Second Chance)

### Concept
CLOCK is a more efficient approximation of LRU. It uses a circular list of pages with a "reference bit" for each page. The algorithm gives recently used pages a "second chance" before replacement.

### Code Walkthrough

```java
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
```

1. **Initialization**:
   - `pageFaults` tracks page faults
   - `pointer` is the "clock hand" that points to the next frame to consider
   - `referenceBits` boolean array tracks if each page has been recently accessed

2. **For each page request**:
   - Check if the page is already in memory

3. **If page is not in memory** (page fault):
   - Increment page fault counter
   - Try to find an empty frame first
   - If found, place the page there, set its reference bit to true, and move the pointer
   
   - **If no empty frame** (all frames are full):
     - Enter a loop to find a victim page using the clock algorithm:
       - Look at the page pointed to by the clock hand
       - **If reference bit is 0** (not recently used):
         - Replace this page with the new page
         - Set the new page's reference bit to 1
         - Move the clock hand to the next position
         - Exit the loop (victim found)
       - **If reference bit is 1** (recently used):
         - Give a "second chance" by setting the reference bit to 0
         - Move the clock hand to the next position
         - Continue the loop (look at the next candidate)

4. **If page is already in memory**:
   - Set its reference bit to 1 (marking it as recently used)

5. **Return** the total number of page faults

### Key Insight
CLOCK simulates LRU by giving pages a "second chance" when they've been recently used. It's more efficient than LRU because it only needs one bit per page and doesn't require sorting or searching through a history list.

## Summary of Algorithms

1. **FIFO**: Simple but doesn't consider page usage
   - Replaces the oldest page first
   - Easy to implement (just a circular pointer)
   - Can suffer from "Belady's anomaly" (more frames can lead to more page faults)

2. **LRU**: Good performance but higher overhead
   - Replaces the least recently used page
   - Requires tracking the full history of page accesses
   - More adaptive to actual usage patterns than FIFO

3. **OPT**: Theoretical best performance but impractical
   - Replaces the page that won't be used for the longest time
   - Requires future knowledge of page accesses
   - Serves as a benchmark for other algorithms

4. **CLOCK**: Good balance of performance and overhead
   - Approximates LRU with less overhead
   - Uses a single bit per page to track recent usage
   - Gives recently used pages a "second chance"

Each algorithm balances different tradeoffs between implementation complexity, memory overhead, and performance in terms of minimizing page faults.
