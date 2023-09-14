package tools.usernamepassworddemo.shared;

public enum UserRole {
  USER("USER"),ADMIN("ADMIN");

    private final String name;


    UserRole(String name) {
        this.name = name;
    }
    public static UserRole getRoleByName(String name){
        for (UserRole value : UserRole.values()) {
            if(value.name.equals(name)){
                return value;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    //a  method named can access , it should return true if the userRole's weight is greater than or equal to its weight
    public boolean canAccess(UserRole userRole){
        return this.ordinal() >= userRole.ordinal();
    }
}
