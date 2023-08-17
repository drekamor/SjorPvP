package dev.drekamor.sjorpvp.config;

public record DatabaseCredentials(String database, String user, String password, String host, int port, int poolSize,
                                  long connectionTimeout, long idleTimeout, long maxLifetime) {
}
