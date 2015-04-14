package symjava.test;

public class TestValueOf {
	public static class Car {
		public int id;
		public Car(int id) {
			this.id = id;
		}
		public Car add(Car c) {
			return new Car(this.id+c.id);
		}
	}
	public static class Jeep extends Car {
		public Jeep(int id) {
			super(id);
		}
		public static Jeep valueOf(Car c) {
			return new Jeep(c.id);
		}
	}
	public static void main(String[] args) {
		Car c1 = new Car(1);
		Car c2 = new Car(10);
		Car c12 = c1 + c2;
		//Jeep j = c12; //works
		//Jeep j = Jeep.valueOf(c1 + c2); //works
		Jeep j = c1 + c2;
		System.out.println(j.id);
		
	}

}
