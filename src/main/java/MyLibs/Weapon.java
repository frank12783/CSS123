package MyLibs;

public class Weapon {
    private String id;
    private String name;
    private int rarity; // 3, 4, or 5
    private double baseMps;
    private int duplicates;
    private String spriteFileName;

    public Weapon(String id, String name, int rarity, double baseMps, String spriteFileName) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.baseMps = baseMps;
        this.duplicates = 0;
        this.spriteFileName = spriteFileName;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getRarity() { return rarity; }
    public double getBaseMps() { return baseMps; }
    public int getDuplicates() { return duplicates; }
    public String getSpriteFileName() { return spriteFileName; }
    
    public void addDuplicate() { this.duplicates++; }

    // 25% MPS increase per duplicate
    public double getEffectiveMps() {
        return baseMps * (1.0 + (0.25 * duplicates));
    }
}