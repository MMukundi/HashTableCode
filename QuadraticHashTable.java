/*
 *  Microassignment: Probing Hash Table addElement and removeElement
 *
 *  QuadraticHashTable: Yet another Hash Table Implementation
 * 
 *  Contributors:
 *    Bolong Zeng <bzeng@wsu.edu>, 2018
 *    Aaron S. Crandall <acrandal@wsu.edu>, 2019
 *    Marcel Mukundi <marcel.mukundi@wsu.edu>, 2020
 * 
 *  Copyright:
 *   For academic use only under the Creative Commons
 *   Attribution-NonCommercial-NoDerivatives 4.0 International License
 *   http://creativecommons.org/licenses/by-nc-nd/4.0
 */

class QuadraticHashTable<K, V> extends HashTableBase<K, V> {
    // Linear and Quadratic probing should rehash at a load factor of 0.5 or higher
    private static final double REHASH_LOAD_FACTOR = 0.5;

    // The number the probing index is multiplied by, ie if QUADRATIC_COEFFICIENT=2,
    // g(i)=2(i^2)+h, where h is the hash value
    private static final int QUADRATIC_COEFFICIENT = 1;

    // The number the probing index squared is added to by, ie if
    // QUADRATIC_CONSTANT=3, g(i)=i^2+h+3, where h is the hash value
    private static final int QUADRATIC_CONSTANT = 0;

    // The final hash then:
    // g(x) = (h+QUADRATIC_COEFFICIENT*(x^2)+QUADRATIC_CONSTANT)%TABLE_SIZE

    // Constructors
    public QuadraticHashTable() {
        super();
    }

    public QuadraticHashTable(HasherBase<K> hasher) {
        super(hasher);
    }

    public QuadraticHashTable(HasherBase<K> hasher, int number_of_elements) {
        super(hasher, number_of_elements);
    }

    // Copy constructor
    public QuadraticHashTable(QuadraticHashTable<K, V> other) {
        super(other);
    }

    // ***** MA Section Start ************************************************ //

    // Concrete implementation for parent's addElement method
    public void addElement(K key, V value) {
        // Check for size restrictions
        resizeCheck();

        // Calculate hash based on key
        int hash = super.getHash(key);

        // The number of failed insertions
        int insertionAttempts = 0;

        // The total number of slots in the table
        final int size = _items.size();

        // The index of the slot currently being probed
        int currentSlotIndex = (hash + QUADRATIC_COEFFICIENT * insertionAttempts * insertionAttempts + QUADRATIC_CONSTANT)
        % size;

        // The indecies visited;
        // The quadratic pattern will not repeat, so instead if every node has been visited, return
        final boolean[] probed = new boolean[size];

        // Continues to probe until the slot the probing has led to is empty
        while (!_items.get(currentSlotIndex).isEmpty()) {
            // If this item is already present, ie the keys match, return
            if (key.equals(_items.get(currentSlotIndex).getKey()))
                return;
            // Increments to the next insertion index
            insertionAttempts++;
            currentSlotIndex = (hash + QUADRATIC_COEFFICIENT * insertionAttempts * insertionAttempts + QUADRATIC_CONSTANT)
            % size;

            // Once every slot allowable by the function has been visited, return
            boolean allProbed = true;
            for(boolean p: probed){
                allProbed = allProbed && p;
            }
            if(allProbed)
            return;
        }

        /*
         * It is ensured that the item is empty because of the while loop It is ensured
         * that the key is not in the table because it was checked at every step in the
         * while loop It is ensured that there is no infinite loop because the index was
         * checked at every step in the while loop Thus, it is safe to add the item to
         * the table and increment the item count
         */
        // Adds the item at the appropriate index
        HashItem<K, V> hItem = _items.get(currentSlotIndex);
        hItem.setKey(key);
        hItem.setValue(value);
        hItem.setIsEmpty(false);

        // Increments the item count
        _number_of_elements++;

        // Remember how many things we are presently storing (size N)
        // Hint: do we always increase the size whenever this function is called?
        // _number_of_elements++;

    }

    // Removes supplied key from hash table
    public void removeElement(K key) {
        // Calculate hash from key
        int hash = super.getHash(key);

        // The number of failed deletions
        int deletionAttempts = 0;

        // The total number of slots in the table
        final int size = _items.size();

        // The index of the slot currently being probed
        int currentSlotIndex = (hash + QUADRATIC_COEFFICIENT * deletionAttempts * deletionAttempts + QUADRATIC_CONSTANT)
                % size;

        // The indecies visited;
        // The quadratic pattern will not repeat, so instead if every node has been visited, return
        final boolean[] probed = new boolean[size];

        // Continues to probe until the slot the probing has led to matches the current
        // one
        while (!key.equals(_items.get(currentSlotIndex).getKey())) {
            // Increments to the next deletion index
            deletionAttempts++;
            currentSlotIndex = (hash + QUADRATIC_COEFFICIENT * deletionAttempts * deletionAttempts + QUADRATIC_CONSTANT)
                    % size;

            // Once every slot allowable by the function has been visited, return
            boolean allProbed = true;
            for(boolean p: probed){
                allProbed = allProbed && p;
            }
            if(allProbed)
            return;
        }

        /*
         * It is ensured that the item key matches this one with the while loop It is
         * ensured that there is no infinite loop because the index was checked at every
         * step in the while loop Thus, it is safe to lazy delete the item and decrement
         * the item count
         */
        // Empties the appropriate index
        HashItem<K, V> hItem = _items.get(currentSlotIndex);
        hItem.setIsEmpty(true);

        // Decrements the item count
        _number_of_elements--;

        // Make sure decrease hashtable size
        // Hint: do we always reduce the size whenever this function is called?
        // _number_of_elements--;

    }

    // ***** MA Section End ************************************************ //

    // Public API to get current number of elements in Hash Table
    public int size() {
        return this._number_of_elements;
    }

    // Public API to test whether the Hash Table is empty (N == 0)
    public boolean isEmpty() {
        return this._number_of_elements == 0;
    }

    // Returns true if the key is contained in the hash table
    public boolean containsElement(K key) {
        int hash = super.getHash(key);
        HashItem<K, V> slot = _items.elementAt(hash);

        // Left incomplete to avoid hints in the MA :)
        return false;
    }

    // Returns the item pointed to by key
    public V getElement(K key) {
        int hash = super.getHash(key);
        HashItem<K, V> slot = _items.elementAt(hash);

        // Left incomplete to avoid hints in the MA :)
        return null;
    }

    // Determines whether or not we need to resize
    // to turn off resize, just always return false
    protected boolean needsResize() {
        // Linear probing seems to get worse after a load factor of about 50%
        if (_number_of_elements > (REHASH_LOAD_FACTOR * _primes[_local_prime_index])) {
            return true;
        }
        return false;
    }

    // Called to do a resize as needed
    protected void resizeCheck() {
        // Right now, resize when load factor > 0.5; it might be worth it to experiment
        // with
        // this value for different kinds of hashtables
        if (needsResize()) {
            _local_prime_index++;

            HasherBase<K> hasher = _hasher;
            QuadraticHashTable<K, V> new_hash = new QuadraticHashTable<K, V>(hasher, _primes[_local_prime_index]);

            for (HashItem<K, V> item : _items) {
                if (item.isEmpty() == false) {
                    // Add element to new hash table
                    new_hash.addElement(item.getKey(), item.getValue());
                }
            }

            // Steal temp hash object's internal vector for ourselves
            _items = new_hash._items;
        }
    }

    // Debugging tool to print out the entire contents of the hash table
    public void printOut() {
        System.out.println(" Dumping hash with " + _number_of_elements + " items in " + _items.size() + " buckets");
        System.out.println("[X] Key	| Value	| Deleted");
        for (int i = 0; i < _items.size(); i++) {
            HashItem<K, V> curr_slot = _items.get(i);
            System.out.print("[" + i + "] ");
            System.out.println(curr_slot.getKey() + " | " + curr_slot.getValue() + " | " + curr_slot.isEmpty());
        }
    }
}