public class MMS {
    private class ListNode {
        String processName;
        int start;
        int size;
        ListNode nextPiece;

        ListNode(String processName, int start, int size) {
            this.processName = processName;
            this.start = start;
            this.size = size;
            nextPiece = null;
        }
    }

    private ListNode freeList;
    private ListNode allocList;
    private int totalFreeMemory;

    public MMS(int size) {
        freeList = new ListNode("", 0, size);
        allocList = null;
        totalFreeMemory = size;
    }

    public void alloc(String name, int size) {
        ListNode prev = null;
        ListNode curr = freeList;
        while (curr != null) {
            if (curr.size >= size) {
                if (curr.size == size) {
                    // Allocate entire free block
                    if (prev == null)
                        freeList = curr.nextPiece;
                    else
                        prev.nextPiece = curr.nextPiece;
                    curr.nextPiece = null;
                } else {
                    // Allocate part of the free block
                    ListNode newNode = new ListNode(name, curr.start, size);
                    curr.start += size;
                    curr.size -= size;
                    curr = newNode;
                }

                // Insert into allocList in order
                if (allocList == null || curr.start < allocList.start) {
                    curr.nextPiece = allocList;
                    allocList = curr;
                } else {
                    ListNode temp = allocList;
                    while (temp.nextPiece != null && curr.start > temp.nextPiece.start) {
                        temp = temp.nextPiece;
                    }
                    curr.nextPiece = temp.nextPiece;
                    temp.nextPiece = curr;
                }

                // Update total free memory
                totalFreeMemory -= size;

                System.out.println("Allocated " + size + " bytes to process " + name);
                return;
            }
            prev = curr;
            curr = curr.nextPiece;
        }

        // If no free block found, perform garbage collection
        GarbageCollection(name, size);
    }

    public void free(String name) {
        ListNode prev = null;
        ListNode curr = allocList;
        boolean found = false;
        while (curr != null) {
            if (curr.processName.equals(name)) {
                totalFreeMemory += curr.size;
                if (prev == null)
                    allocList = curr.nextPiece;
                else
                    prev.nextPiece = curr.nextPiece;
                ListNode temp = freeList;
                freeList = curr;
                freeList.nextPiece = temp;
                curr = prev; // to check the next node after removal
                found = true;
            }
            prev = curr;
            curr = curr.nextPiece;
        }

        if (found)
            System.out.println("Freed all blocks allocated to process " + name);
        else
            System.out.println("Process " + name + " not found");
    }

    public void printalloc() {
        System.out.println("Allocated Blocks:");
        ListNode curr = allocList;
        while (curr != null) {
            System.out.println("Start: " + curr.start + ", Size: " + curr.size + ", Process: " + curr.processName);
            curr = curr.nextPiece;
        }
        System.out.println();
    }

    public void printfree() {
        System.out.println("Free Blocks:");
        ListNode curr = freeList;
        while (curr != null) {
            System.out.println("Start: " + curr.start + ", Size: " + curr.size);
            curr = curr.nextPiece;
        }
        System.out.println();
    }

    private void GarbageCollection(String name, int size) {
        // Perform garbage collection (compaction) to free up contiguous memory
        // Combine all free blocks into one big block and move allocated blocks down

        ListNode curr = allocList;
        int newStart = 0;
        while (curr != null) {
            curr.start = newStart;
            newStart += curr.size;
            curr = curr.nextPiece;
        }

        curr = freeList;
        ListNode prev = null;
        while (curr != null) {
            prev = curr;
            curr = curr.nextPiece;
        }

        prev.nextPiece = new ListNode(name, newStart, size);
        totalFreeMemory = prev.size - size;
        prev.size = size;
        System.out.println("Allocated " + size + " bytes to process " + name + " after garbage collection");
    }

    public static void main(String[] args) {
        MMS myMem = new MMS(100);
        myMem.printalloc();
        myMem.printfree();
        myMem.alloc("pgmA", 50);
        myMem.printalloc();
        myMem.printfree();
        myMem.alloc("pgmB", 20);
        myMem.printalloc();
        myMem.printfree();
        myMem.alloc("pgmA", 10);
        myMem.printalloc();
        myMem.printfree();
        myMem.free("pgmB");
        myMem.printalloc();
        myMem.printfree();
        myMem.alloc("pgmD", 25);
        myMem.printalloc();
        myMem.printfree();
    }
}

