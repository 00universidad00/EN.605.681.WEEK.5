package main.java.edu.jhu.en605681.model;

public enum Hike {
    GARDINER {
        @Override
        public String location() {
            return "Gardiner Lake";
        }

        public String difficulty() {
            return "Intermediate";
        }

        public Integer[] length() {
            return new Integer[]{3, 5};
        }

        public int price() {
            return 40;
        }
    },
    HELLROARING {
        @Override
        public String location() {
            return "Hellroaring Plateau";
        }

        public String difficulty() {
            return "Easy";
        }

        public Integer[] length() {
            return new Integer[]{2, 3, 4};
        }

        public int price() {
            return 35;
        }
    },
    BEATEN {
        @Override
        public String location() {
            return "The Beaten Path";
        }

        public String difficulty() {
            return "Difficult";
        }

        public Integer[] length() {
            return new Integer[]{5, 7};
        }

        public int price() {
            return 45;
        }
    };

    public abstract String location();

    public abstract String difficulty();

    public abstract Integer[] length();

    public abstract int price();
}