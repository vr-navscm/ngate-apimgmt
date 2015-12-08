/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.core.reactor;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.gateway.api.Response;
import io.gravitee.gateway.api.http.BodyPart;
import io.gravitee.reporter.api.metrics.Metrics;

/**
 *
 * @author David BRASSELY (brasseld at gmail.com)
 */
public class HttpServerResponse implements Response {

    private int status;

    private final HttpHeaders headers = new HttpHeaders();

    private final Metrics metrics = new Metrics();

    private boolean chunked = false;

    public int status() {
        return status;
    }

    @Override
    public Response status(int statusCode) {
        this.status = statusCode;
        return this;
    }

    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public Response write(BodyPart bodyPart) {
        return null;
    }

    @Override
    public Response chunked(boolean chunked) {
        this.chunked = chunked;
        return this;
    }

    @Override
    public boolean chunked() {
        return this.chunked;
    }

    @Override
    public void end() {

    }

    @Override
    public Metrics metrics() {
        return metrics;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpServerResponse{");
        sb.append("status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
