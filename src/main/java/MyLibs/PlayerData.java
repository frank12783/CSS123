package MyLibs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerData {
    private double credits = 100.0;
    private double moneyPerClick = 1.0;
    private int pityCounter = 0;
    
    private final int HARD_PITY = 80;
    private final double GACHA_COST = 50.0;
    private final int MAX_EQUIPPED = 10;

    private final Weapon[] equippedGuns = new Weapon[MAX_EQUIPPED];
    private final List<Weapon> reserves = new ArrayList<>();
    private final List<Weapon> gachaPool = new ArrayList<>();
    private final Random random = new Random();

    public PlayerData() {
        initGachaPool();
    }

    private void initGachaPool() {
        // 5-Star Guns (SSR)
        gachaPool.add(new Weapon("wa2000", "WA2000", 5, 25.0, "wa2000.png"));
        gachaPool.add(new Weapon("hk416", "HK416", 5, 30.0, "hk416.png"));
        gachaPool.add(new Weapon("vector", "Vector", 5, 22.0, "vector.png"));
        // 4-Star Guns (SR)
        gachaPool.add(new Weapon("ump45", "UMP45", 4, 8.0, "ump45.png"));
        gachaPool.add(new Weapon("m4a1", "M4A1", 4, 10.0, "m4a1.png"));
        gachaPool.add(new Weapon("kar98k", "Kar98k", 4, 9.0, "kar98k.png"));
        // 3-Star Guns (R)
        gachaPool.add(new Weapon("skorpion", "Skorpion", 3, 2.0, "skorpion.png"));
        gachaPool.add(new Weapon("mp5", "MP5", 3, 2.5, "mp5.png"));
        gachaPool.add(new Weapon("p90", "P90", 3, 3.0, "p90.png"));
    }

    public double getCredits() { return credits; }
    public void addCredits(double amount) { credits += amount; }
    public double getMoneyPerClick() { return moneyPerClick; }
    public int getPity() { return pityCounter; }
    public int getMaxPity() { return HARD_PITY; }
    public Weapon[] getEquippedGuns() { return equippedGuns; }
    public List<Weapon> getReserves() { return reserves; }

    public double calculateTotalMPS() {
        double total = 0.0;
        for (Weapon w : equippedGuns) {
            if (w != null) total += w.getEffectiveMps();
        }
        return total;
    }

    public boolean spendCredits(double amount) {
        if (credits >= amount) {
            credits -= amount;
            return true;
        }
        return false;
    }

    public Weapon pullGacha() {
        pityCounter++;
        double roll = random.nextDouble();
        Weapon pulledTemplate;

        if (pityCounter >= HARD_PITY || roll < 0.02) {
            pulledTemplate = getRandomGunByRarity(5);
            pityCounter = 0; 
        } else if (roll < 0.15) {
            pulledTemplate = getRandomGunByRarity(4);
        } else {
            pulledTemplate = getRandomGunByRarity(3);
        }

        Weapon newWeapon = new Weapon(pulledTemplate.getId(), pulledTemplate.getName(), 
                                      pulledTemplate.getRarity(), pulledTemplate.getBaseMps(), 
                                      pulledTemplate.getSpriteFileName());
        
        processAcquiredWeapon(newWeapon);
        return newWeapon;
    }

    private Weapon getRandomGunByRarity(int rarity) {
        List<Weapon> matching = new ArrayList<>();
        for (Weapon w : gachaPool) {
            if (w.getRarity() == rarity) matching.add(w);
        }
        return matching.get(random.nextInt(matching.size()));
    }

    private void processAcquiredWeapon(Weapon newWeapon) {
        Weapon existing = findOwnedWeapon(newWeapon.getId());
        if (existing != null) {
            existing.addDuplicate();
        } else {
            boolean equipped = false;
            for (int i = 0; i < equippedGuns.length; i++) {
                if (equippedGuns[i] == null) {
                    equippedGuns[i] = newWeapon;
                    equipped = true;
                    break;
                }
            }
            if (!equipped) reserves.add(newWeapon);
        }
    }

    private Weapon findOwnedWeapon(String id) {
        for (Weapon w : equippedGuns) {
            if (w != null && w.getId().equals(id)) return w;
        }
        for (Weapon w : reserves) {
            if (w.getId().equals(id)) return w;
        }
        return null;
    }
}