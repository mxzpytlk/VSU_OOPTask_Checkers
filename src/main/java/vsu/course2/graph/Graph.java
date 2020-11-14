package vsu.course2.graph;

import java.util.*;


public class Graph<T> implements Iterable<T> {
    private List<List<T>> vEdjLists = new ArrayList<>();
    private int eCount = 0;

    public Graph(){ }

    public int vertexCount() {
        return vEdjLists.size();
    }

    public int edgeCount() {
        return eCount;
    }

    public void addEdge(T v1, T v2) {
        int index1 = indexOfVertex(v1);
        int index2 = indexOfVertex(v2);
        if (index1 == -1) {
            vEdjLists.add(new LinkedList<>());
            vEdjLists.get(vEdjLists.size() - 1).add(v1);
            index1 = vEdjLists.size() - 1;
        }

        if (index2 == -1) {
            vEdjLists.add(new LinkedList<>());
            vEdjLists.get(vEdjLists.size() - 1).add(v2);
            index2 = vEdjLists.size() - 1;
        }

        if (!isEdj(v1, v2)) {
            vEdjLists.get(index1).add(vEdjLists.get(index2).get(0));
            vEdjLists.get(index2).add(vEdjLists.get(index1).get(0));
            eCount++;
        }
    }

    public boolean isEdj(T v1, T v2) {
        if (v1 == v2) return true;
        for (T adj : edjacencies(v1)) {
            if (adj.equals(v2)) {
                return true;
            }
        }
        return false;
    }


    public void addVertex(T v) {
        if (indexOfVertex(v) == -1) {
            vEdjLists.add(new LinkedList<>());
            vEdjLists.get(vEdjLists.size() - 1).add(v);
        }
    }

    public T getVertex(T vert) throws GraphException {
        for (T el : bfs(vEdjLists.get(0).get(0))) {
            if (el.equals(vert)) {
                return el;
            }
        }
        throw new GraphException("No such vertex");
    }

    public void removeEdge(T v1, T v2) {
        int index1 = indexOfVertex(v1);
        int index2 = indexOfVertex(v2);
        if (index1 == -1 || index2 == -1 || !vEdjLists.get(index1).contains(vEdjLists.get(index2).get(0))) return;
        vEdjLists.get(index1).remove(vEdjLists.get(index2).get(0));
        eCount--;
    }

    private int indexOfVertex(T v1) {
        for (int i = 0; i < vEdjLists.size(); i++)
            if (vEdjLists.get(i).get(0).equals(v1)) {
                return i;
            };
        return -1;
    }


    public Iterable<T> edjacencies(T v) {
        Iterator<T> it = vEdjLists.get(indexOfVertex(v)).iterator();
        it.next();
        return () -> new Iterator<>() {

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }
        };
    }

    public Iterable<T> bfs() {
        return bfs(vEdjLists.get(0).get(0));
    }

    public Iterable<T> bfs(T from) {
        return new Iterable<>() {
            private Queue<T> queue = null;
            private boolean[] visited = null;

            @Override
            public Iterator<T> iterator() {
                queue = new LinkedList<>();
                queue.add(from);
                visited = new boolean[Graph.this.vertexCount()];
                visited[indexOfVertex(from)] = true;

                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return !queue.isEmpty();
                    }

                    @Override
                    public T next() {
                        T result = queue.remove();
                        for (T adj : Graph.this.edjacencies(result)) {
                            int index = indexOfVertex(adj);
                            if (!visited[index]) {
                                visited[index] = true;
                                queue.add(adj);
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }

    @Override
    public Iterator<T> iterator() {
        return bfs(vEdjLists.get(0).get(0)).iterator();
    }
}
