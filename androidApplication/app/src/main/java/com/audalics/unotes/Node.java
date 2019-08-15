package com.audalics.unotes;

import java.util.List;

/**
 * Created by alexb on 6/14/2017.
 */

public interface Node<T> {
    Node<T> getParent();
    void setParent(Node<T> parent);

    List<Node<T>> getChildren();
    void setChildren(List<Node<T>> children);

    T getData();
    void setData(T data);
}
