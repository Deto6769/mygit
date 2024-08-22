package gxk220025;
/**
 *
 * @author rlarh
 * CS3345.504
 * Project3
 * gxk220025
 * Gwangmo Kim
 */
// If you want to create additional classes, place them in this file as subclasses of MDS

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.List;
import java.util.LinkedList;
import java.util.TreeSet;

public class MDS {
    // Add fields of MDS here
    // Hashmap<id, price>
    // Hashmap <discription, TreeSet<Item>>
    // TreeSet<item>
    
    TreeMap<Integer, Item> id_list; //<id, item>
    HashMap<Integer, TreeSet<Integer>> description_list; //description, id set which contains that description
    
    // Constructors
    public MDS() {
        id_list = new TreeMap<>();
        description_list = new HashMap<>();
        
    }
    
    //sub class: Item<(int) id, (int) price, List(int) disc
    static class Item {
        //field
        private int id;
        private int price;

        
        private List<Integer> description;
        
        //constructor without elements
        public Item() {
            id = 0;
            price = 0;
            //description = new HashSet<>();
            description = new LinkedList<>();
           
        }
        //constructor with elements
        public Item(int id, int price, List<Integer> list){
            this.id = id;
            this.price = price;
            
            this.description = new LinkedList<>();
            
            for(int i = 0; i < list.size(); i++){
                int x = list.get(i);
                this.description.add(x);
                
            }
        }
        
        public int get_id(){
            return this.id;
        }
        public int get_price(){
            return this.price;
        }
        public List<Integer> get_description(){
            return this.description;
        }
        public void change_price(int price){
            this.price = price;
        }
   
    }

    /* Public methods of MDS. Do not change their signatures.
       __________________________________________________________________
       a. Insert(id,price,list): insert a new item whose description is given
       in the list.  If an entry with the same id already exists, then its
       description and price are replaced by the new values, unless list
       is null or empty, in which case, just the price is updated. 
       Returns 1 if the item is new, and 0 otherwise.
    */
    public int insert(int id, int price, java.util.List<Integer> list) {
        /*
        ** case1: id already exist
        */
	if(this.id_list.containsKey(id)) {
            //cas1-a: id exist, but list empty or null then just replace price
            //don't need to record id
            if(list.isEmpty() || (list == null)){
                Item item = new Item(id, price, this.id_list.get(id).description);
                this.id_list.replace(id, item);
                

            }
            //case1-b: id exist and description list not empty
            else{
            //1.remove id from description list which hold ids
                for(int i = 0; i < list.size(); i++){   
                    remove_id(id, list.get(i)); //remove_id method remove id from descriptoin-id hash map's treeset(id)
                }
            //2.replace id with new item which holds new description list
                Item item = new Item(id, price, list);  //create new Item object (id, price, new description list)
                this.id_list.replace(id, item); //replace id-Item tree map
            //3.add description with id
                for(int i = 0; i < list.size(); i++){
                    add_id(id, list.get(i));    //add_id method add id at descriptino-id has map's treeset(id)
                }
                
            }
            return 0;
        }
        /*
        case2: id doesn't exist
        */
        else {
            //put new id with that id's item at map
            Item item = new Item(id, price, list);
            this.id_list.put(id, item);
            
            //record description with id
            if(!list.isEmpty()){
              for(int i = 0; i < list.size(); i++){
                add_id(id, list.get(i));
                }
           
            }
            return 1;
        }
        
    }

    // b. Find(id): return price of item with given id (or 0, if not found).
    public int find(int id) {
        if(this.id_list.containsKey(id)){ //return value's price when id exist
            return this.id_list.get(id).get_price();
        }
        else{
            return 0;
        }
    }

    /* 
       c. Delete(id): delete item from storage.  Returns the sum of the
       ints that are in the description of the item deleted,
       or 0, if such an id did not exist.
    */
    public int delete(int id) {
        //id delete
        if(id_list.containsKey(id)){    //if id and item exist, then get list then return sum of elements of list
            List<Integer> tmp_list = id_list.get(id).get_description();
            int x = 0;  //x is sum of deleted descriptions
            for(int i = 0; i < tmp_list.size(); i++){
                x += tmp_list.get(i);
                int tmp_desc = tmp_list.get(i); 
                remove_id(id, tmp_desc);
                }
            id_list.remove(id);
            return x;
        }
        else{
            return 0;
        }
    }

    //remove_id mtehod remove id from treeset in hashmap(description, treeset(id))
    public void remove_id(int id, int desc){
        if(this.description_list.containsKey(desc)){    //check description hash map contains that key(description)
                TreeSet<Integer> tmp_id_set = this.description_list.get(desc);  //get id set
                tmp_id_set.remove(id);  //remove id from set
                if(tmp_id_set.isEmpty()){  //after remove id, if id set is empty, remove description from map
                    this.description_list.remove(desc);
                }
                else{           //after remove id, still have value in set, then replace it.
                    this.description_list.put(desc, tmp_id_set);
                }
        }
    }
    
    public void add_id(int id, int desc){
        if(!this.description_list.containsKey(desc)){   //if description doesn;t exist, then put new (dsec, id treeset)
            TreeSet<Integer> tmp_id_set = new TreeSet<>();
            tmp_id_set.add(id);
            this.description_list.put(desc, tmp_id_set);
        }
        else{   //description already exists, then just add it and repalce
            TreeSet<Integer> tmp_id_set = this.description_list.get(desc);
            tmp_id_set.add(id);
            this.description_list.replace(desc, tmp_id_set);
        }
    }
    /* 
       d. FindMinPrice(n): given an integer, find items whose description
       contains that number (exact match with one of the ints in the
       item's description), and return lowest price of those items.
       Return 0 if there is no such item.
    */
    public int findMinPrice(int n) {
        if(description_list.containsKey(n)){
            int min_price = 2147483647;  //such maximum price
          
            TreeSet<Integer> tmp_set = description_list.get(n); //get treeset that take id which contatin description n
            Iterator iter = tmp_set.iterator(); //search id in treeset with iterator
            while(iter.hasNext()){  //if id's price is min, then repalce min then return min price
                int tmp_id = (int) iter.next();
                int tmp_price = id_list.get(tmp_id).get_price(); 
                if(tmp_price < min_price){
                    min_price = tmp_price;
                   
                }
            }
            return min_price;
        }
        else
	return 0;
    }

    /* 
       e. FindMaxPrice(n): given an integer, find items whose description
       contains that number, and return highest price of those items.
       Return 0 if there is no such item.
    */
    public int findMaxPrice(int n) {
        if(description_list.containsKey(n)){
            int max_price = -2147483648; //such minimum value
            
            TreeSet<Integer> tmp_set = description_list.get(n); //get treeset that take id which contatin description n
            Iterator iter = tmp_set.iterator(); //search id in treeset with iterator
            while(iter.hasNext()){  //if id's price is min, then repalce max then return max price
                int tmp_id = (int) iter.next();
                int tmp_price = id_list.get(tmp_id).get_price(); 
                if(tmp_price > max_price){
                    max_price = tmp_price;
                    
                }
            }
            return max_price;
        }
        else
	return 0;
    }

    /* 
       f. FindPriceRange(n,low,high): given int n, find the number
       of items whose description contains n, and in addition,
       their prices fall within the given range, [low, high].
    */

    public int findPriceRange(int n, int low, int high){
        if(this.description_list.containsKey(n) && !this.description_list.get(n).isEmpty()){ //description exist and no empty tree set
            int count = 0;
            TreeSet<Integer> tmp_id_set = this.description_list.get(n);
            Iterator iter = tmp_id_set.iterator();
            while(iter.hasNext()){
                int tmp_id = (int) iter.next();
                int tmp_price = this.id_list.get(tmp_id).get_price();
                if(tmp_price <= high && tmp_price >= low){  //conditon [low, high]
                count += 1;
                }
            }
            return count;
        }
        
        else{
            return 0;
        }
    }
    /*
      g. RemoveNames(id, list): Remove elements of list from the description of id.
      It is possible that some of the items in the list are not in the
      id's description.  Return the sum of the numbers that are actually
      deleted from the description of id.  Return 0 if there is no such id.
    */
    public int remove_Names(int id, java.util.List<Integer> list) {
        if(list.isEmpty())  //no serching description
                return 0;
        
        if(id_list.containsKey(id)){    //id exist
            //1.check id
            int sum_des = 0;    //return value sum of rmoved description
            List<Integer> tmp_list = id_list.get(id).get_description();
            for(int i = 0; i < list.size(); i++){
                if(tmp_list.contains(list.get(i))){
                    sum_des += list.get(i);
                    int tmp_desc = list.get(i); //n th element of description
                    
                    if(description_list.containsKey(tmp_desc)){
                        TreeSet<Integer> tmp_set = description_list.get(tmp_desc);  //temporary                    
                        tmp_set.remove(id); //remove id from that description
                        if(tmp_set.isEmpty()){
                            description_list.remove(tmp_desc);
                            }
                        else
                            description_list.replace(tmp_desc, tmp_set); //change description's id set
                    }
                    
                    while(tmp_list.contains(list.get(i))){
                        tmp_list.remove(list.get(i));
                    }
                }
            }
   
            Item tmp_item = new Item(id, id_list.get(id).get_price(), tmp_list);
            id_list.replace(id, tmp_item); //replace id's item with edited description.
 
            return sum_des;
        }
        else
	return 0;
    }
    
    public int removeNames(int id, java.util.List<Integer> list){
 
        if(this.id_list.containsKey(id)){       //check id already exist
            int x = 0;  //return value. sum of acutally removed description.
            List<Integer> tmp_list = this.id_list.get(id).get_description();    //hold tmeporary id's descriptions
            
            //if description list contains remove description, then count it and remove id from dsecription map
            for(int i = 0; i < list.size(); i++){   
                if(tmp_list.contains(list.get(i))){
                    x += list.get(i);
                    remove_id(id, list.get(i));
                    //remove target description from description list
                    while(tmp_list.contains(list.get(i))){
                        tmp_list.remove(list.get(i));
                    }
                }
            }
                Item item = new Item(id, this.id_list.get(id).price, tmp_list); //new item with new descriptions.
                this.id_list.replace(id, item);
            return x;
        }
        else{   //no id
            return 0;
        }
    }
}

