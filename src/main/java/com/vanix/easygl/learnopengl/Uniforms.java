package com.vanix.easygl.learnopengl;

import com.vanix.easygl.graphics.program.UniformChain;

public class Uniforms<T extends Uniforms<T>> {
    public UniformChain<T> model;
    public UniformChain<T> view;
    public UniformChain<T> projection;
    public UniformChain<T> viewPos;
    public UniformChain<T> cameraPos;
    public UniformChain<T> lightPos;
    public Material<Uniforms<T>> material;
    public Material<Uniforms<T>> light;
}
