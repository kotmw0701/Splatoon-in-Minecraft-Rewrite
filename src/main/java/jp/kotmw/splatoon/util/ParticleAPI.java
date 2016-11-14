package jp.kotmw.splatoon.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ParticleAPI extends NMSBase{

	public static class Particle {
		private EnumParticle particle;
		private Location location;
		private float[] diffusion;
		private float speed;
		private int amount;
		private boolean force;

		private Object ParticlesPacket;

		/**
		 * 新しいParticleのインスタンスを生成します。
		 * @param particle パーティクルの種類
		 * @param location 発生位置
		 * @param offsetX x方向への拡散度
		 * @param offsetY y方向への拡散度
		 * @param offsetZ z方向への拡散度
		 * @param speed 速度
		 */
		public Particle(EnumParticle particle, Location location, float offsetX, float offsetY, float offsetZ, float speed) {
			this(particle, location, offsetX, offsetY, offsetZ, speed, 1, false);
		}
		/**
		 * 新しいParticleのインスタンスを生成します。
		 * @param particle パーティクルの種類
		 * @param location 発生位置
		 * @param offsetX x方向への拡散度
		 * @param offsetY y方向への拡散度
		 * @param offsetZ z方向への拡散度
		 * @param speed 速度
		 * @param amount 量
		 */
		public Particle(EnumParticle particle, Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
			this(particle, location, offsetX, offsetY, offsetZ, speed, amount, false);
		}
		/**
		 * 新しいParticleのインスタンスを生成します。
		 * @param particle パーティクルの種類
		 * @param location 発生位置
		 * @param offsetX x方向への拡散度
		 * @param offsetY y方向への拡散度
		 * @param offsetZ z方向への拡散度
		 * @param speed 速度
		 * @param amount 量
		 * @param force 強制描写
		 */
		public Particle(EnumParticle particle, Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount, boolean force) {
			this.particle = particle;
			float[] color = this.particle.getColor();
			this.location = location;
			this.diffusion = color != null ? color : new float[]{offsetX, offsetY, offsetZ};
			this.speed = color != null ? 1f : speed;
			this.amount = color != null ? 0 : amount;
			this.force = force;
			if(is1_7 || is1_8) // 1.9以降ではBukkitの提供するAPIを使うようにしています。もしそれが嫌ならここの条件式だけを除いてください。
				convertPacket();
		}
		private void convertPacket() {
			try {
				if(is1_7) {
					Class<?> a = getNMSClass("PacketPlayOutWorldParticles");
					Constructor<?> b = a.getConstructor(new Class<?>[]{String.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class});
					ParticlesPacket = b.newInstance(new Object[]{getEnumParticle(particle), (float)location.getX(), (float)location.getY(), (float)location.getZ(), diffusion[0], diffusion[1], diffusion[2], speed, amount});
				} else {
					Class<?> a = getNMSClass("EnumParticle");
					Class<?> b = getNMSClass("PacketPlayOutWorldParticles");
					Constructor<?> c = b.getConstructor(new Class<?>[]{a, boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class});
					ParticlesPacket = c.newInstance(new Object[]{getEnumParticle(particle), force, (float)location.getX(), (float)location.getY(), (float)location.getZ(), diffusion[0], diffusion[1], diffusion[2], speed, amount, particle.getRawData()});
				}
			} catch(Throwable ex) {
				ex.printStackTrace();
				ParticlesPacket = null;
			}
		}

		/**
		 * 指定したPlayerに向けてパーティクルを発生させます
		 * @param player パーティクルを描写させたいPlayer
		 * @return 正常に実行されればtrue、そうでなければfalseを返します
		 */
		public boolean sendParticle(Player player) {
			try {
				if(ParticlesPacket != null) {
					sendPacket(player, ParticlesPacket);
				} else {
					player.spawnParticle(org.bukkit.Particle.valueOf(particle.name()), location, amount, diffusion[0], diffusion[1], diffusion[2], speed, particle.getData());
				}
				return true;
			} catch(Throwable ex) {
				ex.printStackTrace();
			}
			return false;
		}
	}

	/**
	 * パーティクルの種類を表す列挙型
	 */
	public static enum EnumParticle {
		EXPLOSION_NORMAL("explode", 0),
		EXPLOSION_LARGE("largeexplode", 1),
		EXPLOSION_HUGE("hugeexplosion", 2),
		FIREWORKS_SPARK("fireworksSpark", 3),
		WATER_BUBBLE("bubble", 4),
		WATER_SPLASH("splash", 5),
		WATER_WAKE("wake", 6),
		SUSPENDED("suspended", 7),
		SUSPENDED_DEPTH("depthsuspend", 8),
		CRIT("crit", 9),
		CRIT_MAGIC("magicCrit", 10),
		SMOKE_NORMAL("smoke", 11),
		SMOKE_LARGE("largesmoke", 12),
		SPELL("spell", 13),
		SPELL_INSTANT("instantSpell", 14),
		SPELL_MOB("mobSpell", 15, DataType.Color),
		SPELL_MOB_AMBIENT("mobSpellAmbient", 16, DataType.Color),
		SPELL_WITCH("witchMagic", 17),
		DRIP_WATER("dripWater", 18),
		DRIP_LAVA("dripLava", 19),
		VILLAGER_ANGRY("angryVillager", 20),
		VILLAGER_HAPPY("happyVillager", 21),
		TOWN_AURA("townaura", 22),
		NOTE("note", 23, DataType.Note),
		PORTAL("portal", 24),
		ENCHANTMENT_TABLE("enchantmenttable", 25),
		FLAME("flame", 26),
		LAVA("lava", 27),
		FOOTSTEP("footstep", 28),
		CLOUD("cloud", 29),
		REDSTONE("reddust", 30, DataType.Color),
		SNOWBALL("snowballpoof", 31),
		SNOW_SHOVEL("snowshovel", 32),
		SLIME("slime", 33),
		HEART("heart", 34),
		BARRIER("barrier", 35),
		ITEM_CRACK("iconcrack", 36, DataType.ItemStack),
		BLOCK_CRACK("blockcrack", 37, DataType.Block),
		BLOCK_DUST("blockdust", 38, DataType.Block),
		WATER_DROP("droplet", 39),
		ITEM_TAKE("take", 40),
		MOB_APPEARANCE("mobappearance",41),
		DRAGON_BREATH("dragonbreath", 42),
		END_ROD("endRod", 43),
		DAMAGE_INDICATOR("damageIndicator", 44),
		SWEEP_ATTACK("sweepAttack", 45),
		FALLING_DUST("fallingdust", 46, DataType.Block),
		;

		private String name;
		private int id;
		private DataType type;
		private int[] numData = null;
		private ItemStack itemData = new ItemStack(Material.IRON_SPADE);
		private MaterialData materialData = new MaterialData(Material.STONE);
		private Color color = null;
		private int pitch = -1;

		private static Map<String, EnumParticle> X;
		private static Map<Integer, EnumParticle> Y;

		static {
			X = new HashMap<String, EnumParticle>();
			Y = new HashMap<Integer, EnumParticle>();
			for(EnumParticle a : values()){
				X.put(a.name, a);
				Y.put(a.id, a);
			}
		}
		private EnumParticle(String name, int id) {
			this(name, id, DataType.None);
		}
		private EnumParticle(String name, int id, DataType type) {
			this.name = name;
			this.id = id;
			this.type = type;
			switch(this.type) {
			case ItemStack:
				numData = new int[]{256, 0};
				break;
			case Block:
				numData = new int[]{1, 0};
				break;
			default: break;
			}
		}

		/**
		 * パーティクルの名前を返します。<br>
		 * ここでの名前はparticleコマンドで指定する名前です。
		 * @return パーティクルの名前を返します
		 */
		public String getName() {
			if(hasDataParticle() && is1_7) {
				return name+"_"+numData[0]+"_"+numData[1];
			} else return name;
		}

		/**
		 * パーティクルの数値IDを返します。
		 * @return パーティクルの数値IDを返します
		 */
		public int getID() {
			return id;
		}

		/**
		 * このパーティクルが、1.8で追加された新しいパーティクルであるかを返します
		 * @return このパーティクルが1.8で追加されたものならtrue、そうでなければfalseを返します
		 */
		public boolean is1_8NewParticle() {
			switch (this) {
			case BARRIER:
			case WATER_DROP:
			case ITEM_TAKE:
			case MOB_APPEARANCE: return true;
			default: return false;
			}
		}

		/**
		 * このパーティクルが、1.9で追加された新しいパーティクルであるかを返します
		 * @return このパーティクルが1.9で追加されたものならtrue、そうでなければfalseを返します
		 */
		public boolean is1_9NewParticle() {
			switch (this) {
			case DRAGON_BREATH:
			case END_ROD:
			case DAMAGE_INDICATOR:
			case SWEEP_ATTACK: return true;
			default: return false;
			}
		}

		/**
		 * このパーティクルが、1.10で追加された新しいパーティクルであるかを返します
		 * @return このパーティクルが1.10で追加されたものならtrue、そうでなければfalseを返します
		 */
		public boolean is1_10NewParticle() {
			return this == EnumParticle.FALLING_DUST;
		}

		/**
		 * このパーティクルが、データ値を持つパーティクルであるかを返します。
		 * @return このパーティクルがデータ値を持てばtrue、そうでなければfalseを返します
		 */
		public boolean hasDataParticle() {
			switch (this.type) {
			case ItemStack:
			case Block: return true;
			default: return false;
			}
		}

		/**
		 * このパーティクルのデータ値に、引数のItemStackの情報を割り当てます。
		 * @param item 適用させたい情報を持つItemStack
		 * @return データ値変更後のEnumParticle
		 */
		@SuppressWarnings("deprecation")
		public EnumParticle setItemData(ItemStack item) {
			if(item == null) throw new NullPointerException("`item` is null.");
			if(type == DataType.ItemStack) {
				itemData = item;
				numData[0] = item.getTypeId();
				numData[1] = (int)item.getDurability();
			}
			return this;
		}

		/**
		 * このパーティクルのデータ値に、引数のBlockの情報を割り当てます。
		 * @param block 適用させたい情報を持つBlock
		 * @return データ値変更後のEnumParticle
		 */
		@SuppressWarnings("deprecation")
		public EnumParticle setBlockData(Block block) {
			if(block == null) throw new NullPointerException("`block` is null.");
			switch (this.type) {
			case ItemStack:
				return setItemData(new ItemStack(block.getType(), 1, (short)block.getData()));
			case Block:
				materialData = block.getState().getData();
				numData[0] = block.getTypeId();
				numData[1] = (int)block.getData();
			default: return this;
			}
		}

		/**
		 * このパーティクルのデータ値に、引数のMaterialの情報を割り当てます。
		 * @param material 適用させたい情報を持つMaterial
		 * @return データ値変更後のEnumParticle
		 */
		@SuppressWarnings("deprecation")
		public EnumParticle setMaterialData(Material material) {
			if(material == null) throw new NullPointerException("`material` is null.");
			switch (this.type) {
			case ItemStack:
				return setItemData(new ItemStack(material));
			case Block:
				materialData = new MaterialData(material);
				numData[0] = material.getId();
				numData[1] = 0;
			default: return this;
			}
		}

		/**
		 * このパーティクルのデータ値を設定します。
		 * @param id 設定したいアイテムID
		 * @return データ値変更後のEnumParticle
		 */
		public EnumParticle setNumberData(int id) {
			return setNumberData(id, 0);
		}

		/**
		 * このパーティクルのデータ値を設定します。
		 * @param id 設定したいアイテムID
		 * @param data 設定したいダメージ値
		 * @return データ値変更後のEnumParticle
		 */
		@SuppressWarnings({ "deprecation", "unused" })
		public EnumParticle setNumberData(int id, int data) {
			switch (this.type) {
			case ItemStack:
				return setItemData(new ItemStack(id, 1, (short)data));
			case Block:
				MaterialData md = new MaterialData(id, (byte)data);
				if(md == null) throw new NullPointerException("`MaterialData` is null.");
				materialData = md;
				numData[0] = id;
				numData[1] = data;
			default: return this;
			}
		}

		/**
		 * 1.9以降用のデータ
		 * @return ItemStackやMaterialData
		 */
		private Object getData() {
			switch (this.type) {
			case ItemStack: return itemData;
			case Block: return materialData;
			default: return null;
			}
		}
		/**
		 * 数値データ
		 * @return ItemIDとDamage値の配列
		 */
		private int[] getRawData() {
			switch (this.type) {
			case ItemStack: return numData;
			case Block: return new int[]{ numData[1]*4096 + numData[0] };
			default: return null;
			}
		}

		/**
		 * 一部のパーティクルの色を指定します。
		 * <p>色の指定が出来るのは{@link EnumParticle.SPELL_MOB}("mobSpell")や{@link EnumParticle.REDSTONE}("reddust")といった一部のパーティクルだけです。<br>
		 * 色を指定すると、拡散度と速度、発生するパーティクルが1回に固定されます。</p>
		 * @param color 指定する色
		 * @return EnumParticle
		 */
		public EnumParticle setColor(Color color) {
			if(color == null) throw new NullPointerException("`color` is null.");
			if(this.type == DataType.Color) this.color = color;
			return this;
		}
		/**
		 * {@link EnumParticle.NOTE}のパーティクルの色を指定します。
		 * <p>{@link EnumParticle.NOTE}("note")専用です。<br>
		 * 色を指定すると、拡散度と速度、発生するパーティクルが1回に固定されます。<br>
		 * 値は0以上24以下で指定されます。</p>
		 * @param pitch 指定する色
		 * @return EnumParticle
		 */
		public EnumParticle setPitch(int pitch) {
			if(this.type == DataType.Note && pitch >= 0 && pitch <= 24) this.pitch = pitch;
			return this;
		}
		/**
		 * 色の設定を適用させるためのもの
		 * @return 設定がないならnull
		 */
		private float[] getColor() {
			switch (this.type) {
			case Color:
				if(color == null) return null;
				float[] colorData = new float[]{color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f};
				if(this == EnumParticle.REDSTONE) colorData[0] -= 1f;
				return colorData;
			case Note:
				if(pitch < 0) return null;
				return new float[]{pitch/24f, 0f, 0f};
			default: return null;
			}
		}

		/**
		 * 文字列から該当するパーティクルの種類を返します
		 * @param name パーティクルの名前
		 * @return 見つからない場合はnullが返されます
		 */
		public static EnumParticle getParticle(String name) {
			String[] divi = name.split("_");
			int len = divi.length;
			if(len == 2 || len == 3) {
				EnumParticle par = getParticle(divi[0]);
				if(par != null && par.hasDataParticle()) {
					int id = 1;
					int data = 0;
					try {
						if(len == 2) {
							int num = Integer.parseInt(divi[1]);
							if(num >= 4096) {
								id = num / 4096;
								data = num % 4096;
							} else {
								id = num;
							}
						} else if(len == 3) {
							id = Integer.parseInt(divi[1]);
							data = Integer.parseInt(divi[2]);
						}
					} catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {}
					par.setNumberData(id, data);
				}
				return par;
			}
			return X.containsKey(name) ? X.get(name) : null;
		}

		/**
		 * 数値IDから該当するパーティクルの種類を返します
		 * @param id パーティクルの数値ID
		 * @return 見つからない場合はnullが返されます
		 */
		public static EnumParticle getParticle(int id) {
			return Y.containsKey(id) ? Y.get(id) : null;
		}
	}

	private static String version;
	private static String NMSPackageName;
	private static boolean is1_7 = false;
	private static boolean is1_8 = false;
	//private static boolean is1_9 = false;
	static {
		version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		NMSPackageName = "net.minecraft.server."+version;
		is1_7 = version.startsWith("v1_7_R");
		is1_8 = version.startsWith("v1_8_R");
		//is1_9 = version.startsWith("v1_9_R");
	}
	private static Object getEnumParticle(EnumParticle par) throws Exception {
		if(is1_7) {
			return par.getName();
		} else {
			int i = par.getID();
			Class<?> EnumParticleClass = getNMSClass("EnumParticle");
			Method a = EnumParticleClass.getDeclaredMethod("a", int.class);
			Object EnumParticle = a.invoke(EnumParticleClass, i);
			return EnumParticle;
		}
	}

	private static enum DataType {
		ItemStack,
		Block,
		Color,
		Note,
		None
	}
}
