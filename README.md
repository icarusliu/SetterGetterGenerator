Bean的Getter与Setter方法生成工具
针对每一个属性，生成三个方法：常规的Getter与Setter方法以及Builder方式的Setter方法；
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
目前生成Setter与Getter方法，IDEA本身能支持Builder的方式，但多数反射操作类都未考虑非void的Setter方法，因此在某些应用场景下会失效。通过本插件针对每一个属性生成三个方法，可以即使用Builder方式又可以兼容所有的反射处理类。

插件安装后在Window菜单下会增加子菜单，可以设定快捷键使用。