package net.myfigurecollection.api.legacy;

public interface RequestListener {
    public abstract void onRequestcompleted(int requestCode, Object result);
}