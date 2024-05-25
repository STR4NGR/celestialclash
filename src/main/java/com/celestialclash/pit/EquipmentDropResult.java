package com.celestialclash.pit;

public class EquipmentDropResult {
    private final Equipment equipment;
    private final int index;

    public EquipmentDropResult(Equipment equipment) {
        this.equipment = equipment;
        this.index = equipment.getIndex();
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public int getIndex() {
        return index;
    }
}
