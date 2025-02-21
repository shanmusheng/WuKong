package com.p1nero.wukong.capability;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class WKPlayer {
    private String lastSkill = "";//用于恢复闪避技能
    private boolean perfectDodge;
    private float damageReduce = -1;
    private final List<Integer> fakeWukongIds = new ArrayList<>();
    private int cooldownQiShu, cooldownShenFa, cooldownHaoMao;

    public int getCooldownQiShu() {
        return cooldownQiShu;
    }

    public void setCooldownQiShu(int cooldownQiShu) {
        this.cooldownQiShu = cooldownQiShu;
    }

    public int getCooldownShenFa() {
        return cooldownShenFa;
    }

    public void setCooldownShenFa(int cooldownShenFa) {
        this.cooldownShenFa = cooldownShenFa;
    }

    public int getCooldownHaoMao() {
        return cooldownHaoMao;
    }

    public void setCooldownHaoMao(int cooldownHaoMao) {
        this.cooldownHaoMao = cooldownHaoMao;
    }

    public void addFakeWukongId(int id){
        fakeWukongIds.add(id);
    }

    public List<Integer> getFakeWukongIds() {
        return fakeWukongIds;
    }

    public void setDamageReduce(float damageReduce) {
        this.damageReduce = damageReduce;
    }

    public float getDamageReduce() {
        return damageReduce;
    }

    public void setLastDodgeSkill(String lastSkill) {
        this.lastSkill = lastSkill;
    }

    public String getLastDodgeSkill() {
        return lastSkill;
    }

    public void setPerfectDodge(boolean perfectDodge) {
        this.perfectDodge = perfectDodge;
    }

    public boolean isPerfectDodge() {
        return perfectDodge;
    }

    public void saveNBTData(CompoundTag tag){
        tag.putString("lastSkill", lastSkill);
    }

    public void loadNBTData(CompoundTag tag){
        lastSkill = tag.getString("lastSkill");
    }

    public void copyFrom(WKPlayer old){
        lastSkill = old.lastSkill;
    }

    public void copyCooldown(WKPlayer old){
        cooldownQiShu = old.cooldownQiShu;
        cooldownShenFa = old.cooldownShenFa;
        cooldownHaoMao = old.cooldownHaoMao;
    }

}
