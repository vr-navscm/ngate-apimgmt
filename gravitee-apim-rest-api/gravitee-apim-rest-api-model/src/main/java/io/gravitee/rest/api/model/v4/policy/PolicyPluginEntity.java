/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.model.v4.policy;

import io.gravitee.rest.api.model.platform.plugin.PlatformPluginEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "PolicyPluginEntityV4")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class PolicyPluginEntity extends PlatformPluginEntity {

    private Set<FlowPhase> proxy;

    private Set<FlowPhase> message;
}
