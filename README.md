# How to use FirebaseParent?

## What is the purpose?

When you want to upload class to [Firebase Realtime Database][Firebase Realtime Database], you need to upload the class
as Json, but you also can with Hashmap. if you want to upload "Simple class", that with only Primitive Data Types and String.
you can upload automatically with no time. 

So where are the problem? when you need to upload "Complicated class", that with another class as attribute or int, 
double, String, or also with ArrayList, you have to convert the class into HashMap (or Json), in order to upload the 
class to [Firebase Realtime Database][Firebase Realtime Database]. like:

```java
public HashMap<String,Object> toHashMap() {
    HashMap<String,Object> map = new HashMap<>();
    map.put("email",this.email);

    HashMap<String,Object> missionsMap = new HashMap<>();
    for(int i=0; i<this.missions.size(); i++)
    missionsMap.put("mission"+i,this.missions.get(i).toMap());
    map.put("missions",missionsMap);

    HashMap<String,Object> guestsMap = new HashMap<>();
    for(int i = 0; i< this.guests.size(); i++)
    guestsMap.put("guest"+i, this.guests.get(i).toMap());
    map.put("guests",guestsMap);

    HashMap<String,Object> managerMap = new HashMap<>();
    managerMap.put("name",this.manager.name)
    managerMap.put("phone",this.manager.phone)
    managerMap.put("id",this.manager.id)
    map.put("manager",managerMap);
    return map;
}
```

And when you get the data from [Firebase Realtime Database][Firebase Realtime Database], you get the data as HashMap (or
Json). So, you have to convert the HashMap to the class object. like:

```java
public Manager(HashMap<String, Object> map) {
    this.email = String.valueOf(map.get("email"));

    int i=0;
    HashMap<String,Object> missionsMap = (HashMap<String, Object>) map.get("missions");
    this.missions = new ArrayList<>();
    if(missionsMap!=null)
        while(missionsMap.get("mission"+i)!=null) {
            this.missions.add(new Mission((HashMap<String, Object>) missionsMap.get("mission"+i)));
            i++;
        }

    i=0;
    HashMap<String,Object> guestsMap = (HashMap<String, Object>) map.get("guests");
    this.guests = new ArrayList<>();
    if(guestsMap!=null)
        while(guestsMap.get("guest"+i)!=null) {
            this.guests.add(new Guest((HashMap<String, Object>) guestsMap.get("guest"+i)));
            i++;
        }
    HashMap<String,Object> managerMap = map.get("manager");
    this.manager = new Manager(String.valueOf(managerMap.get("name")),String.valueOf(managerMap.get("phone")),String.valueOf(managerMap.get("id")));
}
```

The problem that it will take a lot of waste of time. So, the class I created `FirebaseParent` to solve the problem.
When you want to upload "Complicated class" as I described already, you only need to make that class extends from
`FirebaseParent` class. in addition, you need to title the method like that:

```java
public class FBUser extends FirebaseParent {
    public String uid;
    public String email;
    [...]
    public ArrayList<String> followingUid;
    public ArrayList<String> followersUid;

    public FBUser(HashMap<String, Object> map) {
        super(map);
    }
    
    public HashMap<String, Object> toHashMap() {
        return super.toHashMap();
    }
    
    [...]
}
```

Now you can get & set "Complicated class" on [Firebase Realtime Database][Firebase Realtime Database] with a lot of save time like that:

get:
```java
// temp is instance of FirebaseParent
ValueEventListener listener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // YOUR_CLASS is instance of FirebaseParent
        temp = new YOUR_CLASS((HashMap<String, Object>) (dataSnapshot.getValue()));
    }
    [...]
};
```

set:
```java
// temp is instance of FirebaseParent
FirebaseDatabase.getInstance().getReference(YOUR_REFERNCE)
.child(temp.getKey()).setValue(temp.toHashMap());
```

## The way the class converted:
1. For non-ArrayList & non-`FirebaseParent` class attribute: the key of the `HashMap` will be the name of the attribute
2. For ArrayList attribute, at the end of the name attribute, the `*L` string will add for the key in the `HashMap`. In addition, every object in the list, will get key value of the first letter in name class, and index number.
3. For `FirebaseParent` attribute, at the end of the name attribute, the `*P=t:` string will add for the key in the `HashMap`. In addition, the package and name class with added too with `=>` sign.

### example:
```json
"--KEY_OBJECT--": {
      "comments*L": {
        "c0*P=t:uriya => madmoni => app => models => Comment": {
          "date*P=t:uriya => madmoni => app => models => FBDate": {
            "day": 13,
            "month": 4,
            "year": 2003
          },
          [...]
        }
      },
      "description": "Hello There, I hope you happy with my work :)",
      "key": "--KEY_OBJECT--",
      "photoPlace": "POSTS/-NhY-HnKeT4OVPfjbiUb",
      "photoUrl": "https://firebasestorage.googleapis.com/v0/b/courseinstegramapp.appspot.com/o/POSTS%2F-NhY-HnKeT4OVPfjbiUb?alt=media&token=18e82665-736c-4565-aca2-ba382ec2ef49",
      "tags*L": {
        "t0": "The best android studio code ever!"
      },
      [...]
    }
} 
```

## Rules
1. Don't put `=>`, `*L` or `*P=t:` in attribute name.
2. All fields of "child-class \ subclass" MUST be public.
3. All attribute can only be:
   1. Primitive Data Types.
   2. `FirebaseParent` child-class / sub-class.
   3. `ArrayList` of simple Primitive Data Types.
   4. `ArrayList` of `FirebaseParent` child-class / sub-class.

[Firebase Realtime Database]: https://firebase.google.com/products/realtime-database

# Attention!
This is a beta version! so, mistakes & and crush can be made. 
Please, send to me every mistake you find on my email: `uriya.work@gmail.com`

