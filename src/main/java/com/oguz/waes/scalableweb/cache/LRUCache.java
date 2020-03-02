package com.oguz.waes.scalableweb.cache;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * LRU cache stand for Least Recently Used Cache. which evict least recently used entry.
 * As Cache purpose is to provide fast and efficient way of retrieving data. it need to meet certain requirement.
 * <p>
 * Some of the Requirement are
 * Fixed Size: Cache needs to have some bounds to limit memory usages.
 * Fast Access: Cache Insert and lookup operation should be fast , preferably O(1) time.
 * Replacement of Entry in case , Memory Limit is reached: A cache should have efficient algorithm to
 * evict the entry when memory is full.
 */
@Component
@Log4j2
public class LRUCache<K, V> {

    private int lruSize = 250;

    private static class Node<T, U> {
        U value;
        T key;
        Node<T, U> left;
        Node<T, U> right;
    }


    private HashMap<K, Node<K, V>> hashmap;
    private Node<K, V> start, end;

    // implementation, it can make be dynamic
    public LRUCache() {
        hashmap = new HashMap<>();
    }

    public void evict(K key) {
        if (hashmap.containsKey(key)) {
            Node<K, V> node = hashmap.get(key);
            removeNode(node);
            hashmap.remove(key);
        }
    }

    public V getEntry(K key) {
        if (hashmap.containsKey(key)) // Key Already Exist, update the value and move it to top
        {
            Node<K, V> node = hashmap.get(key);
            removeNode(node);
            addAtTop(node);
            return node.value;
        }
        return null;
    }

    public void putEntry(K key, V value) {
        if (hashmap.containsKey(key)) // Key Already Exist, just update the value and move it to top
        {
            Node<K, V> node = hashmap.get(key);
            node.value = value;
            removeNode(node);
            addAtTop(node);
        } else {
            Node<K, V> newnode = new Node<>();
            newnode.left = null;
            newnode.right = null;
            newnode.value = value;
            newnode.key = key;
            if (hashmap.size() > lruSize) // We have reached maxium size so need to make room for new element.
            {
                hashmap.remove(end.key);
                removeNode(end);
                addAtTop(newnode);

            } else {
                addAtTop(newnode);
            }

            hashmap.put(key, newnode);
        }
    }

    private void addAtTop(Node<K, V> node) {
        node.right = start;
        node.left = null;
        if (start != null)
            start.left = node;
        start = node;
        if (end == null)
            end = start;
    }

    private void removeNode(Node<K, V> node) {

        if (node.left != null) {
            node.left.right = node.right;
        } else {
            start = node.right;
        }

        if (node.right != null) {
            node.right.left = node.left;
        } else {
            end = node.left;
        }
    }

    public V getStart() {
        return start.value;
    }

    public V getEnd() {
        return end.value;
    }
}
