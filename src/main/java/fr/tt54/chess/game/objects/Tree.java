package fr.tt54.chess.game.objects;

import java.util.HashSet;
import java.util.Set;

public class Tree<T> {

    private T value;
    private final Set<Tree<T>> children;

    public Tree(T value, Set<Tree<T>> sons) {
        this.value = value;
        this.children = sons;
    }

    public Tree(T value){
        this.children = new HashSet<>();
    }

    public Tree(Set<Tree<T>> sons){
        this.value = null;
        this.children = sons;
    }

    public Tree(){
        this.value = null;
        this.children = new HashSet<>();
    }

    public void addChild(Tree<T> child){
        children.add(child);
    }

    public void removeChild(Tree<T> child){
        children.remove(child);
    }

    public boolean isLeaf(){
        return children.size() == 0;
    }

    public Set<Tree<T>> getChildren() {
        return children;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
