package models.entities.components.inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.App;
import models.entities.Entity;
import models.entities.components.EntityComponent;
import models.entities.components.Pickable;
import models.enums.EntityTag;
import models.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class Inventory extends EntityComponent implements Serializable {
    @JsonProperty("slots")
    private ArrayList<InventorySlot> slots = new ArrayList<>();
    @JsonProperty("capacity")
    //with the current system there is no need for this field. but i'll keep it anyway
    private int capacity;
    @JsonProperty("unlimited")
    private boolean unlimited;


    public Inventory(int capacity) {
        this(capacity, false);
    }
    public Inventory(int capacity, boolean unlimited) {
        this.slots = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            this.slots.add(new InventorySlot());
        }
        this.capacity = capacity;

        this.setUnlimited(unlimited);
    }

    public Inventory(ArrayList<InventorySlot> slots) {
        this.slots.addAll(slots);
        this.capacity = slots.size();
    }

    public void setUnlimited(boolean unlimited) {
        this.unlimited = unlimited;
        if(unlimited){
            while(slots.size() < 20){
                slots.add(new InventorySlot());
            }
            if(getEmptySpace() == 0) addSlot();
        }
    }

    public boolean getUnlimited() {
        return unlimited;
    }

    private Inventory(Inventory other) {
        this.capacity = other.capacity;
        for (int i = 0; i < capacity; i++) {
            this.slots.add(new InventorySlot());
        }
        this.setUnlimited(other.unlimited);
    }

    public Inventory() {
        this(0);
    }

    public Entity addItemToSlot(Entity entity, InventorySlot slot) {
        Pickable pickable = entity.getComponent(Pickable.class);
        if(pickable == null){
            throw new RuntimeException("trying to add a " + entity.getEntityName() + " to an slot, but it doesnt have a" +
                    "component.");
        }

        Entity entity2 = slot.getEntity();
        if (entity2 == null) {
            if(pickable.getStackSize() > pickable.getMaxStack()){
                slot.setEntity(pickable.split(pickable.getMaxStack()));
                return entity;
            }
            slot.setEntity(entity.clone());
            entity.delete();
            return null;
        }

        if (entity2.isTheSameAs(entity)) {
            Pickable pickable2 = entity2.getComponent(Pickable.class);
            int leftSpace = pickable2.getMaxStack() - pickable2.getStackSize();
            int picked = Math.min(leftSpace, pickable.getStackSize());
            pickable2.changeStackSize(picked);
            pickable.changeStackSize(-picked);
            if (pickable.getStackSize() == 0) {
                return null;
            } else {
                return entity;
            }
        }

        return entity;
    }
    public boolean canAddItem(Entity item, int amount) {
        if(unlimited) return true;
        for (InventorySlot slot : slots) {
            if(slot.getEntity() != null && slot.getEntity().isTheSameAs(item)) {
                Pickable slotPickable = slot.getEntity().getComponent(Pickable.class);
                amount -= slotPickable.getMaxStack() - slotPickable.getStackSize();
            }
        }
        for (InventorySlot slot : slots) {
            if(slot.getEntity() == null) {
                amount -= item.getComponent(Pickable.class).getMaxStack();
            }
        }
        return amount <= 0;
    }
    public boolean canAddItem(Entity item) {
        Pickable pickable = item.getComponent(Pickable.class);
        if(pickable == null) return false;
        return canAddItem(item, pickable.getStackSize());
    }

    public Entity addItem(Entity entity) {
        if (entity == null) {
            return null;
        }
        if(entity.getComponent(Pickable.class).getStackSize() == 0)
            entity.getComponent(Pickable.class).setStackSize(1);
        Pickable pickable = entity.getComponent(Pickable.class);
        if (pickable == null) {
            throw new RuntimeException("Tried to pick up " + entity.getEntityName() + " which doesnt have a Pickable component");
        }

        //add to existing stacks of the same item if possible
        for (InventorySlot s : this.slots) {
            Entity entity2 = s.getEntity();
            if (entity2 != null && entity2.isTheSameAs(entity)) {
                if((entity = addItemToSlot(entity, s)) == null){
                    break;
                }
            }
        }
        if(entity != null){
            //add to empty slots in inventory
            if (pickable.getStackSize() != 0) {
                for (InventorySlot s : slots) {
                    if (s.getEntity() == null) {
                        if((entity = addItemToSlot(entity, s)) == null){
                            break;
                        }
                    }
                }
            }
        }
        if(entity == null){
            //add an extra slot
            if(unlimited && getEmptySpace()==0){
                this.addSlot();
            }
            return null;
        }

        if(!unlimited) return entity;

        //add slots
        while(entity != null){
            InventorySlot newSlot = this.addSlot();
            entity = addItemToSlot(entity, newSlot);
        }

        //add an extra slot
        this.addSlot();

        return null;
    }
    public Entity addItem(Entity entity, int quantity) {
        entity.getComponent(Pickable.class).setStackSize(quantity);
        return addItem(entity);
    }
    public int getEmptySpace(){
        int emptySlots = 0;
        for(InventorySlot s : slots){
            if(s.getEntity() == null){
                emptySlots++;
            }
        }
        return emptySlots;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        while (slots.size() < capacity){
            slots.add(new InventorySlot());
        }
    }
    public InventorySlot addSlot(){
        InventorySlot newSlot = new InventorySlot();
        this.slots.add(newSlot);
        return newSlot;
    }
//    public Entity removeItem(Entity entity) {
//        for (InventorySlot s : slots) {
//            if (s.getEntity().equals(entity)) {
//                s.setEntity(null);
//            }
//        }
//        return entity;
//    }

//    public Entity removeItem(String entityName, int amount) {
//        // TODO: remove amount number of entity with entityName and return it
//        Entity entity = null;
//        for (InventorySlot s : slots) {
//            if (s.getEntity()!= null && s.getEntity().getName().equals(entityName)) {
//                s.setEntity(null);
//            }
//        }
//        return entity;
//    }

    public ArrayList<InventorySlot> getSlots() {
        return this.slots;
    }
    public ArrayList<Entity> getEntities() {
        ArrayList<Entity> out = new ArrayList<>(slots.size());
        for (InventorySlot s : slots) {
            if (s.getEntity() != null) {
                out.add(s.getEntity());
            }
        }
        return out;
    }
    public ArrayList<Entity> getItemsByTag(EntityTag tag) {
        ArrayList<Entity> out = new ArrayList<>(slots.size());
        for (InventorySlot s : slots) {
            if (s.getEntity() != null && s.getEntity().hasTag(tag)) {
                out.add(s.getEntity());
            }
        }
        return out;
    }

    public Entity getItem(String name) {
        ArrayList<Entity> out = new ArrayList<>(slots.size());
        for (InventorySlot s : slots) {
            if (s.getEntity() != null && StringUtils.isNamesEqual(s.getEntity().getEntityName(), name)) {
                return s.getEntity();
            }
        }
        return null;
    }
    public Entity getItem(Entity entity) {
        ArrayList<Entity> out = new ArrayList<>(slots.size());
        for (InventorySlot s : slots) {
            if (s.getEntity() != null && s.getEntity().isTheSameAs(entity)) {
                return s.getEntity();
            }
        }
        return null;
    }

    public InventorySlot getSlot(String name) {
        for (InventorySlot s : slots) {
            if (s.getEntity() != null && StringUtils.isNamesEqual(s.getEntity().getEntityName(), name)) {
                return s;
            }
        }
        return null;
    }
    public InventorySlot getSlot(Entity e) {
        return getSlot(e.getEntityName());
    }

    private void removeExtraSpaces(){
        if(unlimited){
            while(slots.size() > 20 && (getEmptySpace() > 1)){
                for(InventorySlot s : slots){
                    if(s.getEntity() == null){
                        slots.remove(s);
                        break;
                    }
                }
            }
        }
    }

    public Entity takeFromInventory(Entity entity, int amount){
        if(!this.doesHaveItem(entity)) return null;
        if(getItemCount(entity) < amount) return null;

        Entity takenEntity = entity.clone();
        Pickable pickable = takenEntity.getComponent(Pickable.class);
        pickable.setStackSize(0);

        for (InventorySlot s : slots) {
            Entity e = s.getEntity();
            if(e == null) continue;
            if(e.isTheSameAs(entity)){
                int stackSize = e.getComponent(Pickable.class).getStackSize();
                if(stackSize < amount){
                    e.delete();
                    amount -= stackSize;
                    pickable.changeStackSize(stackSize);
                }else{
                    e.getComponent(Pickable.class).changeStackSize(-amount);
                    pickable.changeStackSize(amount);
                    amount = 0;
                    break;
                }

            }
        }

        removeExtraSpaces();

        return takenEntity;
    }
    public Entity takeFromInventory(String entityName, int amount) {
        if(!this.doesHaveItem(entityName)) return null;
        if(getItemCount(entityName) < amount) return null;

        Entity takenEntity = App.entityRegistry.makeEntity(entityName);
        Pickable pickable = takenEntity.getComponent(Pickable.class);
        pickable.setStackSize(0);

        for (InventorySlot s : slots) {
            Entity e = s.getEntity();
            if(e == null) continue;
            if(StringUtils.isNamesEqual(e.getEntityName(), entityName)) {
                int stackSize = e.getComponent(Pickable.class).getStackSize();
                if(stackSize < amount){
                    e.delete();
                    amount -= stackSize;
                    pickable.changeStackSize(stackSize);
                }else{
                    e.getComponent(Pickable.class).changeStackSize(-amount);
                    pickable.changeStackSize(amount);
                    amount = 0;
                    break;
                }

            }
        }

        removeExtraSpaces();

        return takenEntity;
    }

    public boolean doesHaveItem(String name, int amount) {
        int count = getItemCount(name);
        return count >= amount;
    }
    public boolean doesHaveItem(String name) {
        return this.getItem(name) != null;
    }
    public boolean doesHaveItem(Entity entity){
        return this.getItem(entity) != null;
    }

    public int getItemCount(String name){
        int count = 0;
        for(InventorySlot s : slots){
            if(s.getEntity() != null){
                if(StringUtils.isNamesEqual(s.getEntity().getEntityName(), name)){
                    count += s.getEntity().getComponent(Pickable.class).getStackSize();
                }
            }
        }
        return count;
    }
    public int getItemCount(Entity entity){
        int count = 0;
        for(InventorySlot s : slots){
            if(s.getEntity() != null){
                if(s.getEntity().isTheSameAs(entity)){
                    count += s.getEntity().getComponent(Pickable.class).getStackSize();
                }
            }
        }
        return count;
    }
    public void empty(){
        for(InventorySlot slot : slots){
            if(slot.getEntity() == null) continue;
            slot.getEntity().delete();
        }
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        int i = 1;
        for (InventorySlot s : getSlots()) {
            Entity entity = s.getEntity();
            out.append(String.format("%-2d: ", i));
            if (entity != null) {
                out.append(String.format("%s \t%d", entity.getEntityName(), entity.getComponent(Pickable.class).getStackSize()));
            } else {
                out.append(String.format("-"));
            }
            if (App.getActiveGame().getCurrentPlayer().getActiveSlot() == s) {
                out.append(String.format(" <active>"));
            }
            out.append(String.format("\n"));
            i++;
        }
        return out.toString();
    }

    public int getSize() {
        return slots.size();
    }

    @Override
    public EntityComponent clone() {
        return new Inventory(this);
    }
}
