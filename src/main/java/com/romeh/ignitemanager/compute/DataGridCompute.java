package com.romeh.ignitemanager.compute;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteException;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteReducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * generic utility class for map reduce call
 */
@Component
public class DataGridCompute {

    @Autowired
    private Ignite ignite;

    /**
     * @param jobs the list of jobs to be distributed into the data grid nodes from the master node
     * @param igniteReducer the ignite reducer which will be used to determine the reduction and collection logic
     * @param callback the callback to be invoked upon receiving the reduced final response
     * @param <R> generic response type from the jobs
     * @param <E> generic map reduced response type
     * @throws IgniteException
     *
     * a generic async map reduced call inside ignite compute grid
     */
    public <R, E> void executeMapReduceFailFast(Collection<IgniteCallable<R>> jobs, IgniteReducer<R, E> igniteReducer, Consumer<E> callback) throws IgniteException {
        // you need to define your cluster group and if any defined in your data grid
        IgniteCompute igniteCompute = ignite.compute(ignite.cluster().forPredicate(clusterNode -> !clusterNode.isClient()));
        //execute the list of jobs in map reduce fashion and pass the custom reducer as well
        IgniteFuture<E> future=igniteCompute.callAsync(jobs, igniteReducer);
        // then async listen for the result to invoke your post call back
        future.listen(result -> callback.accept(result.get()));
    }


    /**
     * @param jobs the list of jobs to be distributed into the data grid nodes from the master node
     * @param igniteReducer the ignite reducer which will be used to determine the reduction and collection logic
     * @param <R> generic response type from the jobs
     * @param <E> generic map reduced response type
     * @throws IgniteException
     * @return <E> generic map reduced response type
     * a generic sync map reduced call inside ignite compute grid
     */
    public <R, E> E executeMapReduceFailFastSync(Collection<IgniteCallable<R>> jobs, IgniteReducer<R, E> igniteReducer) throws IgniteException {
        // you need to define your cluster group and if any defined in your data grid
        IgniteCompute igniteCompute = ignite.compute(ignite.cluster().forPredicate(clusterNode -> !clusterNode.isClient()));
        //execute the list of jobs in map reduce fashion and pass the custom reducer as well
        return igniteCompute.call(jobs, igniteReducer);
    }
}
