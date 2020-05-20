Bean的Getter与Setter方法生成工具

针对Bean的每一个属性，都会生成三个方法：常规的Getter与Setter方法以及Builder方式的Setter方法；
如：
```java
    public SacrificeDTO id(Long id) {
        this.id = id;
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }
```
目前生成Setter与Getter的方式中，IDEA本身就能支持生成Builder类型的Setter方法，但很多反射操作类在查找Setter方法时都未考虑这种方式的Setter方法，因此在调用反射设置对象属性时会失效。通过本插件，为每一个属性生成三个方法，解决这个问题。

插件安装后在Window菜单下会增加子菜单，可以设定快捷键使用。