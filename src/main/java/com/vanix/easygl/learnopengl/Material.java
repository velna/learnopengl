package com.vanix.easygl.learnopengl;

import com.vanix.easygl.commons.Chained;
import com.vanix.easygl.graphics.program.UniformChain;

public class Material<T> extends Chained.Simple<T> {
    public UniformChain<Material<T>> ambient;
    public UniformChain<Material<T>> diffuse;
    public UniformChain<Material<T>> specular;
    public UniformChain<Material<T>> shininess;
    public UniformChain<Material<T>> emission;
    public UniformChain<Material<T>> position;
    public UniformChain<Material<T>> direction;

    public Material(T owner) {
        super(owner);
    }

}
