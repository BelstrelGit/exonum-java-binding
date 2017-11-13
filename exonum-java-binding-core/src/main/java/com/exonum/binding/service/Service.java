package com.exonum.binding.service;

import com.exonum.binding.messages.BinaryMessage;
import com.exonum.binding.messages.Transaction;
import com.exonum.binding.storage.database.Fork;
import com.exonum.binding.storage.database.Snapshot;
import com.exonum.binding.storage.indices.ProofListIndexProxy;
import com.exonum.binding.storage.indices.ProofMapIndexProxy;
import io.vertx.ext.web.Router;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * An Exonum service.
 *
 * <p>You shall usually subclass an {@link AbstractService} which implements some of the methods
 * declared in this interface.
 */
public interface Service {

  /**
   * Returns the id of the service.
   */
  short getId();

  /**
   * Returns the name of the service.
   */
  String getName();

  /**
   * Initializes the service. This method is called once when a genesis block is created
   * and is supposed to
   * <ul>
   *   <li>(a) initialize the database schema of this service, and</li>
   *   <li>(b) provide an initial <a href="https://exonum.com/doc/architecture/services/#global-configuration">global configuration</a>
   * of the service.</li>
   * </ul>
   *
   * <p>The service configuration parameters must be provided as a JSON string.
   * It is recorded in a table of global configuration parameters of each service deployed
   * in the network.
   *
   * @param fork a database fork to apply changes to. Not valid after this method returns
   * @return a global configuration of the service, or {@code Optional.empty()} if the service
   *         does not have any configuration parameters.
   * @see <a href="https://exonum.com/doc/architecture/services/#genesis-block-handler">Genesis block handler</a>
   */
  default Optional<String> initialize(Fork fork) {
    return Optional.empty();
  }

  /**
   * Converts an Exonum transaction message to an executable transaction of <em>this</em> service.
   *
   * @param message a transaction message
   *                (i.e., whose message type is a transaction and service id is set to the id of
   *                this service)
   * @return an executable transaction
   * @throws IllegalArgumentException if the message is not a transaction of this service
   * @throws NullPointerException if message is null
   */
  Transaction convertToTransaction(BinaryMessage message);

  /**
   * Returns a list of root hashes of all Merklized tables defined by this service,
   * as of the given snapshot of the blockchain state. If the service doesn't have any Merklized
   * tables, returns an empty list.
   *
   * <p>The core uses this list to aggregate hashes of tables defined by all services
   * into a single Merklized meta-map.  The hash of this meta-map is considered the hash
   * of the entire blockchain state and is recorded as such in blocks and Precommit messages.
   *
   * @param snapshot a snapshot of the blockchain state. Not valid after this method returns
   * @see ProofListIndexProxy#getRootHash()
   * @see ProofMapIndexProxy#getRootHash()
   */
  // fixme: byte[] -> HashCode
  default List<byte[]> getStateHashes(Snapshot snapshot) {
    return Collections.emptyList();
  }

  /**
   * Creates handlers that makes up the public API of this service.
   * The handlers are added to the given router, which is then mounted at a path,
   * equal to the service name.
   *
   * <p>Please note that the path prefix is stripped from the request path when it is forwarded to
   * the given router. For example, if your service name is «cryptocurrency»,
   * and you have two endpoints «/send-money» and «/balance», use these names when
   * defining handlers, and they will be available by paths «/cryptocurrency/send-money»
   * and «/cryptocurrency/balance»:
   * <pre><code>
   * router.get("/balance").handler((rc) -> {
   *   rc.response().end("$1’000’000");
   * });
   * </code></pre>
   *
   * @param node a set-up Exonum node, providing an interface to access
   *             the current blockchain state and submit transactions
   * @param router a router responsible for handling requests to this service
   */
  void createPublicApiHandlers(Node node, Router router);
}