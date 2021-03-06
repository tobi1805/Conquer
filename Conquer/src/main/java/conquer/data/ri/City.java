package conquer.data.ri;

import conquer.data.ConquerInfo;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.data.Resource;
import conquer.data.Shared;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class City implements ICity {
	private static final int MAX_VARIANCE = 101;
	private static final long BASE_POPULATION = 100L;
	private static final int PEOPLE_THRESHOLD = 50;
	private double bonus = -1;
	private int clanId = -1;
	private double defense = -1;
	private final ConquerInfo game;
	private double growth;
	private Image image;
	private List<Integer> levels = new ArrayList<>();
	private String name;
	private long numAttacksOfPlayer = -1;
	private long numberOfPeople;
	private long numberOfSoldiers;
	private List<Double> productions;
	private int numberOfRoundsWithZeroPeople = 0;
	private int x = -1;
	private int y = -1;
	private double oldOne = 1;
	private IClan clan;

	/**
	 * Create a new City.
	 *
	 * @param game A handle to the game object
	 */
	City(final ConquerInfo game) {
		if (game == null) {
			throw new IllegalArgumentException("game==null");
		}
		for (var i = 0; i < (Resource.values().length + 1); i++) {
			this.levels.add(0);
		}
		this.game = game;
	}

	/**
	 * Increases the number of attacks of the player, Internal Use only!
	 */
	void attackByPlayer() {
		if (this.numAttacksOfPlayer == -1) {
			this.numAttacksOfPlayer++;
		}
		this.numAttacksOfPlayer++;
	}

	/**
	 * Compares one city with another. The comparison is quite simple. The strength
	 * of each city is calculated by adding the defense to the product of the
	 * defense bonus and the number of soldiers in the city.
	 */
	public int compareTo2(final City other) {
		if (other == null) {
			throw new IllegalArgumentException("other==null");
		}
		return Double.compare((this.getNumberOfSoldiers() * this.getBonus()) + this.getDefense(),
				(other.getNumberOfSoldiers() * other.getBonus()) + other.getDefense());
	}

	@Override
	public int compareTo(final ICity other) {
		if (other instanceof City c) {
			return this.compareTo2(c);
		}
		return 0;
	}

	/**
	 * Called at the end of the round.
	 */
	@Override
	public void endOfRound() {
		if (this.numberOfPeople <= City.PEOPLE_THRESHOLD) {
			this.numberOfRoundsWithZeroPeople++;
			if (this.numberOfRoundsWithZeroPeople == 3) {
				this.numberOfRoundsWithZeroPeople = 0;
				this.setNumberOfPeople(City.BASE_POPULATION + Shared.getRandomNumber(City.MAX_VARIANCE));
			}
		} else {
			this.numberOfRoundsWithZeroPeople = 0;
		}
	}

	/**
	 * Returns whether one city is equals to the other one. Not all properties are
	 * checked, only the name, the X- and Y-position.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof conquer.data.ri.City other)) {
			return false;
		}
		return other.name.equals(this.name) && (other.x == this.x) && (other.y == this.y);
	}

	/**
	 * Gives the defensebonus: Suppose there are 500 Soldiers in the city and there
	 * is a defense bonus of 1.03. Now they have the same power as 515 soldiers.
	 *
	 * @return The defensebonus
	 */
	@Override
	public double getBonus() {
		return this.bonus;
	}

	/**
	 * Returns the clan of the city.
	 *
	 * @return The clan.
	 */
	@Override
	public IClan getClan() {
		return this.clan;
	}

	/**
	 * Returns the clan id.
	 *
	 * @return The clan id
	 */
	@Override
	public int getClanId() {
		return this.clanId;
	}

	/**
	 * Get the difference between the production of coins per round and the use of
	 * coins per round.
	 *
	 * @return The difference
	 */
	@Override
	public double getCoinDiff() {
		return (this.numberOfPeople * this.game.getResourceUsage(this.clan).getCoinsPerRoundPerPerson())
				- (this.numberOfSoldiers * this.game.getSoldierCosts(this.clan).coinsPerSoldierPerRound());
	}

	/**
	 * Gives the base defense: The base defense means, that even if a city has no
	 * soldiers, it has a bit of defense. (Think of towers and walls)
	 *
	 * @return The base defense
	 */
	@Override
	public double getDefense() {
		return this.defense;
	}

	/**
	 * Returns the strength of a city based on its own values and the clan.
	 *
	 * @return The defense strength of the city.
	 */
	@Override
	public double getDefenseStrength() {
		return this.getDefense() + (this.getNumberOfSoldiers() * this.getBonus() * this.clan.getSoldiersStrength()
				* this.clan.getSoldiersDefenseStrength());
	}

	/**
	 * Returns the growth of a city. The growth of a city is the factor, for which
	 * the number of persons increases every round. For example: 1000 Persons and a
	 * growth of 1.01: Next round: 1010 persons<br>
	 * Next round: 1020 persons<br>
	 *
	 * @return The growth of the city.
	 */
	@Override
	public double getGrowth() {
		return this.growth;
	}

	/**
	 * Returns the icon of this city.
	 *
	 * @return The icon of the city.
	 */
	@Override
	public Image getImage() {
		return this.image;
	}

	/**
	 * Returns a handle to the information object
	 *
	 * @return A handle to the information object.
	 */
	@Override
	public ConquerInfo getInfo() {
		return this.game;
	}

	/**
	 * Returns the number of levels of each resource of the city.
	 *
	 * @return The levels
	 */
	@Override
	public List<Integer> getLevels() {
		return this.levels;
	}

	/**
	 * Get the name of the city
	 *
	 * @return The name of the city.
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Returns how often the city was attacked by the player
	 *
	 * @return Number of attacks of the player.
	 */
	public long getNumberAttacksOfPlayer() {
		return this.numAttacksOfPlayer;
	}

	/**
	 * Returns the number of persons in the city
	 *
	 * @return Number of persons in the city.
	 */
	@Override
	public long getNumberOfPeople() {
		return this.numberOfPeople;
	}

	/**
	 * Returns the amount of rounds with a population less than a certain amount of
	 * people.
	 *
	 * @return Number of rounds with a small population.
	 */
	int getNumberOfRoundsWithZeroPeople() {
		return this.numberOfRoundsWithZeroPeople;
	}

	/**
	 * Returns the number of soldiers in the city.
	 *
	 * @return Number of soldiers in this city
	 */
	@Override
	public long getNumberOfSoldiers() {
		return this.numberOfSoldiers;
	}

	/**
	 * Gives the how much every resource is produced every round.
	 *
	 * @return A mutable list with the production rates.
	 */
	@Override
	public List<Double> getProductions() {
		return this.productions;
	}

	/**
	 * Returns the x-Position
	 *
	 * @return x-Position
	 */
	@Override
	public int getX() {
		return this.x;
	}

	/**
	 * Returns the y-Position.
	 *
	 * @return y-Position
	 */
	@Override
	public int getY() {
		return this.y;
	}

	/**
	 * Returns the hashcode. Only the x-Position and y-Position are used because of
	 * performance reasons and the requirement, that no city has the same position
	 * as another city.
	 *
	 * @return The hashcode.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.x, this.y);
	}

	/**
	 * Returns whether this city is owned by the player.
	 *
	 * @return {@code true} if the player owns this city.
	 */
	@Override
	public boolean isPlayerCity() {
		return this.clan.isPlayerClan();
	}

	/**
	 * Returns some internal value. <mark><b>Shouldn't be called from
	 * outside</b></mark>
	 *
	 * @return Internal value.
	 */
	double oldOne() {
		return this.oldOne;
	}

	/**
	 * Get the production of a resource per round
	 *
	 * @param resource Resource - May not be null
	 * @return The production of a resource per round.
	 */
	@Override
	public double productionPerRound(final Resource resource) {
		if (resource == null) {
			throw new IllegalArgumentException("resource == null");
		}
		return this.productions.get(resource.getIndex()) * this.numberOfPeople;
	}

	/**
	 * Sets the number of attacks of the player. Can only be called once.
	 *
	 * @param num New number of attacks of the player.
	 */
	public void setAttacksOfPlayer(final long num) {
		if (this.numAttacksOfPlayer != -1) {
			throw new UnsupportedOperationException(
					"Can't change the number of attacks of the player after it was set!");
		}
		this.numAttacksOfPlayer = num;
	}

	/**
	 * Set the clan of the city.
	 *
	 * @param clan May not be null.
	 */
	@Override
	public void setClan(final IClan clan) {
		if (clan == null) {
			throw new IllegalArgumentException("clan == null");
		}
		this.clanId = clan.getId();
		this.clan = clan;
	}

	/**
	 * Updates the value of the defense
	 *
	 * @param base New value.
	 */
	@Override
	public void setDefense(final double base) {
		if (base < 0) {
			throw new IllegalArgumentException("argument < 0");
		}
		if (this.defense == 0) {
			this.defense = base;
		} else {
			this.defense /= this.oldOne;
			this.defense *= (base < 1 ? 1 / base : base);
			this.oldOne = base;
		}
		this.defense = base;
	}

	/**
	 * Changes the defensebonus. Can only be called once. Otherwise it will throw an
	 * {@link UnsupportedOperationException}
	 *
	 * @param bonus The value.
	 */
	public void setDefenseBonus(final double bonus) {
		if (this.bonus != -1) {
			throw new UnsupportedOperationException("Can't change the defense bonus of a city!");
		}
		this.bonus = bonus;
	}

	/**
	 * Sets the growth of this city.
	 *
	 * @param growth The new value. May not be smaller than zero.
	 */
	@Override
	public void setGrowth(final double growth) {
		if (growth < 0) {
			throw new IllegalArgumentException("growth < 0");
		}
		this.growth = growth;
	}

	void setId(final int id) {
		this.clanId = id;
	}

	/**
	 * Changes the image. Can only be called once, else an
	 * {@link UnsupportedOperationException} will be thrown.
	 *
	 * @param image The image. May not be null.
	 */
	public void setImage(final Image image) {
		if (this.image != null) {
			throw new UnsupportedOperationException("Can't change image of city!");
		} else if (image == null) {
			throw new IllegalArgumentException("image == null");
		}
		this.image = image;
	}

	/**
	 * Sets the levels of each resource.
	 *
	 * @param levels
	 */
	public void setLevels(final List<Integer> levels) {
		this.levels = levels;
	}

	/**
	 * Sets the name of the city. Can only be called once.
	 *
	 * @param name Name of the city.
	 */
	public void setName(final String name) {
		if (this.name != null) {
			throw new UnsupportedOperationException("Can't change name of city!");
		}
		this.name = name;
	}

	/**
	 * Set the number of people in the city.
	 *
	 * @param num May not be negative
	 */
	@Override
	public void setNumberOfPeople(final long num) {
		if (num < 0) {
			throw new IllegalArgumentException("num < 0 : " + num);
		}
		this.numberOfPeople = num;
	}

	/**
	 * Internal use only!
	 *
	 * @param num
	 */
	void setNumberOfRoundsWithZeroPeople(final int num) {
		this.numberOfRoundsWithZeroPeople = num;
	}

	/**
	 * Set the number of soldiers in the city.
	 *
	 * @param num May not be negative
	 */
	@Override
	public void setNumberOfSoldiers(final long num) {
		if (num < 0) {
			throw new IllegalArgumentException("num < 0 : " + num);
		}
		this.numberOfSoldiers = num;
	}

	/**
	 * Internal use only!
	 *
	 * @param value
	 */
	void setOldValue(final double value) {
		this.oldOne = value;
	}

	/**
	 * Sets the productionrates.
	 *
	 * @param productions
	 */
	public void setProductionRates(final List<Double> productions) {
		if (this.productions != null) {
			throw new UnsupportedOperationException("Can't change the productions!");
		} else if (productions == null) {
			throw new IllegalArgumentException("productions == null");
		} else if (productions.size() != Resource.values().length) {
			throw new IllegalArgumentException(
					"Wrong length, expected " + Resource.values().length + ", got " + productions.size());
		}
		this.productions = new GoodDoubleList(productions);
	}

	/**
	 * Sets the x-Position. May only be called once.
	 *
	 * @param x
	 */
	public void setX(final int x) {
		if (this.x != -1) {
			throw new UnsupportedOperationException("Can't change the X-Position!");
		} else if (x < 0) {
			throw new IllegalArgumentException("x < 0");
		}
		this.x = x;
	}

	/**
	 * Sets the x-Position. May only be called once.
	 *
	 * @param y
	 */
	public void setY(final int y) {
		if (this.y != -1) {
			throw new UnsupportedOperationException("Can't change the Y-Position!");
		} else if (y < 0) {
			throw new IllegalArgumentException("y < 0");
		}
		this.y = y;
	}

	@Override
	public String toString() {
		return "City [image=" + this.image + ", clan=" + this.clanId + ", numberOfPeople=" + this.numberOfPeople
				+ ", numberOfSoldiers=" + this.numberOfSoldiers + ", y=" + this.y + ", x=" + this.x + ", defense="
				+ this.defense + ", bonus=" + this.bonus + ", name=" + this.name + ", growth=" + this.growth + "]";
	}
}
