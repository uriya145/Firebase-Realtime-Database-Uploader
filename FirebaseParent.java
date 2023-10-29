import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (c) 2023. Uriya Madmoni.
 * All rights reserved.
 * git-hub-link:
 */

public class FirebaseParent {
    private final String IS_ARRAYLIST = "*L";
    private final String IS_FIREBASE_PARENT_CLASS = "*P=t:";
    private final String PACKAGE_DOT_REPLACE = " => ";

    public FirebaseParent() {

    }

    /**
     * @param map      present child class of FirebaseParent class
     * @param keyClass the name of the class, get the class name from that parameter with "getClassByKeyMap" for secure.
     * @return class object
     * @Look getClassByKeyMap(String key) for better understanding.
     */
    private Object generateFirebaseParentFromMap(HashMap<String, Object> map, String keyClass) {
        try {
            return Class.forName(getClassByKeyMap(keyClass)).getConstructor(HashMap.class).newInstance(map);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param map present list of objects.
     * @return the map as arraylist for create class object.
     */
    private ArrayList<Object> generateArrayListFromMap(HashMap<String, Object> map, String letter) {
        ArrayList<Object> temp = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Object> subSet : map.entrySet()) {
            if (subSet.getKey().contains(IS_FIREBASE_PARENT_CLASS)) {
                try {
                    temp.add(generateFirebaseParentFromMap((HashMap<String, Object>) subSet.getValue(), subSet.getKey()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else temp.add(subSet.getValue());
        }
        return temp;
    }

    /**
     * @param key formatted with IS_FIREBASE_PARENT_CLASS.
     * @return class name.
     */
    private String getClassByKeyMap(String key) {
        return key.substring(key.indexOf(IS_FIREBASE_PARENT_CLASS) + IS_FIREBASE_PARENT_CLASS.length())
                .replace(PACKAGE_DOT_REPLACE,".");
    }

    /**
     * @param messKey key with IS_ARRAYLIST, IS_FIREBASE_PARENT_CLASS, etc.
     * @return key with-out all the additional those Strings.
     */
    private String cleanUpKey(String messKey) {
        if (messKey.contains(IS_FIREBASE_PARENT_CLASS))
            return messKey.substring(0, messKey.indexOf(IS_FIREBASE_PARENT_CLASS) + IS_FIREBASE_PARENT_CLASS.length()).replace(IS_FIREBASE_PARENT_CLASS, "");
        return messKey.replace(IS_ARRAYLIST, "").replace(IS_FIREBASE_PARENT_CLASS, "");
    }

    /**
     * @param map with IS_ARRAYLIST and IS_FIREBASE_PARENT_CLASS keys.
     * @Result set the map with-out those key.
     * @implNote the method to return the map. because, the map is a pointer, so when it changed here, it changed everywhere.
     */
    private void cleanUpMap(HashMap<String, Object> map) {
        map.remove(IS_ARRAYLIST);
        map.remove(IS_FIREBASE_PARENT_CLASS);
    }

    /**
     * @param map for firebase Keys must not contain '/', '.', '#', '$', '[', or ']', so when we convert to hashmap, we replace the '.' with '=>'
     *            '.' can upper in class name where the class inside package, like models.FBDate.
     *            we replace the problem letter when you insert the value.
     * @implNote the method to return the map. because, the map is a pointer, so when it changed here, it changed everywhere.
     */
    private void putFixToMap(HashMap<String, Object> map, String key, Object value) {
        map.put(key.replace(".", PACKAGE_DOT_REPLACE), value);
    }

    /**
     * @param map create class object form the map [HashMap]
     */
    public FirebaseParent(HashMap<String, Object> map) {
        cleanUpMap(map);
        for (Map.Entry<String, Object> set : map.entrySet()) {
            if (set.getKey().contains(IS_ARRAYLIST))
                setField(cleanUpKey(set.getKey()),
                        generateArrayListFromMap((HashMap<String, Object>) set.getValue(), set.getKey().substring(0, 1)));
            else if (set.getKey().contains(IS_FIREBASE_PARENT_CLASS)) {
                try {
                    setField(cleanUpKey(set.getKey()), generateFirebaseParentFromMap((HashMap<String, Object>) set.getValue(), set.getKey()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else setField(set.getKey(), set.getValue());
        }
        setParentNullFields();
        setNullFields();
    }

    /**
     * go through on every declared field in class, at set the null ArrayList to "new ArrayList<>()"
     */
    private void setNullFields() {
        for (Field f : this.getClass().getDeclaredFields()) {
            Field field;
            try {
                field = getClass().getDeclaredField(f.getName());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            try {
                if (field.getGenericType().toString().contains("ArrayList")
                        && field.get(this) == null) {
                    setField(field.getName(), new ArrayList<>());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * go through on every field in the parent class, at set the null ArrayList to "new ArrayList<>()"
     */
    private void setParentNullFields() {
        for (Field f : this.getClass().getFields()) {
            Field field;
            try {
                field = getClass().getField(f.getName());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            try {
                if (field.getGenericType().toString().contains("ArrayList")
                        && field.get(this) == null) {
                    setParentField(field.getName(), new ArrayList<>());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void printFields() {
        for (Field f : this.getClass().getDeclaredFields()) {
            Field field;
            try {
                field = getClass().getDeclaredField(f.getName());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            try {
                System.out.println("name: " + field.getName());
                System.out.println("value: " + field.get(this));
                System.out.println("class: " + field.getGenericType());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        for (Field f : this.getClass().getFields()) {
            Field field;
            try {
                field = getClass().getField(f.getName());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            try {
                System.out.println("name: " + field.getName());
                System.out.println("value: " + field.get(this));
                System.out.println("class: " + field.getGenericType());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @return return the String that description the class as hashmap design.
     */
    @Override
    public String toString() {
        return this.toHashMap().toString();
    }

    /**
     * @return return the class object as hashmap in order to upload the class to firebase.
     * @implSpec
     */
    public HashMap<String, Object> toHashMap() {
        FirebaseParent temp;
        ArrayList<Object> tempList;
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> tempMap;
        for (Field f : this.getClass().getDeclaredFields()) {
            Field field;
            try {
                field = getClass().getDeclaredField(f.getName());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            try {
                if (field.get(this) instanceof FirebaseParent) {
                    temp = (FirebaseParent) field.get(this);
                    putFixToMap(map, field.getName() + IS_FIREBASE_PARENT_CLASS + field.get(this).getClass().getName(), temp.toHashMap());
                } else if (field.get(this) instanceof ArrayList) {
                    tempList = (ArrayList) field.get(this);
                    tempMap = new HashMap<>();
                    for (int i = 0; i < tempList.size(); i++) {
                        if (tempList.get(i) instanceof FirebaseParent) {
                            temp = (FirebaseParent) tempList.get(i);
                            putFixToMap(tempMap,String.valueOf(field.getName().substring(0, 1) + i) +
                                            IS_FIREBASE_PARENT_CLASS +
                                            tempList.get(i).getClass().getName(),
                                    temp.toHashMap());
                        } else putFixToMap(tempMap, String.valueOf(field.getName().substring(0, 1) + i), tempList.get(i));
                    }
                    putFixToMap(map, field.getName() + IS_ARRAYLIST, tempMap);
                } else {
                    putFixToMap(map, field.getName(), field.get(this));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        for (Field f : this.getClass().getFields()) {
            Field field;
            try {
                field = getClass().getField(f.getName());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            try {
                if (field.get(this) instanceof FirebaseParent) {
                    temp = (FirebaseParent) field.get(this);
                    putFixToMap(map, field.getName() + IS_FIREBASE_PARENT_CLASS + field.get(this).getClass().getName(), temp.toHashMap());
                } else if (field.get(this) instanceof ArrayList) {
                    tempList = (ArrayList) field.get(this);
                    tempMap = new HashMap<>();
                    for (int i = 0; i < tempList.size(); i++) {
                        if (tempList.get(i) instanceof FirebaseParent) {
                            temp = (FirebaseParent) tempList.get(i);
                            putFixToMap(tempMap,String.valueOf(field.getName().substring(0, 1) + i) +
                                            IS_FIREBASE_PARENT_CLASS +
                                            tempList.get(i).getClass().getName(),
                                    temp.toHashMap());
                        } else putFixToMap(tempMap, String.valueOf(field.getName().substring(0, 1) + i), tempList.get(i));
                    }
                    putFixToMap(map, field.getName() + IS_ARRAYLIST, tempMap);
                } else {
                    putFixToMap(map, field.getName(), field.get(this));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return map;
    }


    /**
     * set field class by his name
     */
    private void setField(String fieldName, Object value) {
        try {
            Field field = getClass().getDeclaredField(fieldName.replace(PACKAGE_DOT_REPLACE,"."));
            field.set(this, value);
        } catch (Exception e) {
            System.out.println("ERROR: " + this.getClass().getName() + " => setField: " + e);
        }
    }

    /**
     * set parent field class by his name
     */
    private void setParentField(String fieldName, Object value) {
        try {
            Field field = getClass().getField(fieldName.replace(PACKAGE_DOT_REPLACE,"."));
            field.set(this, value);
        } catch (Exception e) {
            System.out.println("ERROR: " + this.getClass().getName() + " => setParentField: " + e);
        }
    }
}
