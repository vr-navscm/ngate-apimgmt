/*
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
import * as _ from 'lodash';
import AnalyticsService from '../../services/analytics.service';
import { ApiService } from '../../services/api.service';
import EventsService from '../../services/events.service';

const WidgetComponent: ng.IComponentOptions = {
  template: require('./widget.html'),
  bindings: {
    widget: '<',
    updateMode: '<',
    globalQuery: '<',
    customTimeframe: '<',
  },
  controller: function ($scope, $state, AnalyticsService: AnalyticsService, EventsService: EventsService, ApiService: ApiService) {
    'ngInject';
    this.$state = $state;
    this.AnalyticsService = AnalyticsService;
    this.EventsService = EventsService;
    this.ApiService = ApiService;

    $scope.$on('gridster-resized', () => {
      $scope.$broadcast('onWidgetResize');
    });

    this.$onChanges = (changes) => {
      if (changes.customTimeframe && changes.customTimeframe.currentValue) {
        this.customTimeframe = changes.customTimeframe.currentValue;
        this.changeTimeframe(this.customTimeframe);
        // We need to send this event to compute the size of the widget when input data changes.
        setTimeout(() => {
          $scope.$broadcast('onWidgetResize');
        }, 100);
      }
    };

    $scope.$on('onTimeframeChange', (event, timeframe) => {
      this.changeTimeframe(timeframe);
    });

    const unregisterFn = $scope.$on('onQueryFilterChange', (event, query) => {
      if (this.widget.chart && this.widget.chart.request) {
        this.widget.chart.request.query = query.query;
        this.reload();
      }
    });

    $scope.$on('$destroy', unregisterFn);

    this.changeTimeframe = (timeframe) => {
      if (this.widget.chart && this.widget.chart.request) {
        let query;

        if (this.widget.chart.request.query) {
          query = this.widget.chart.request.query;
        }

        // Associate the new timeframe to the chart request
        _.assignIn(this.widget.chart.request, {
          interval: timeframe.interval,
          from: timeframe.from,
          to: timeframe.to,
          query: query,
        });
        this.reload();
      }
    };

    this.reload = () => {
      // Call the analytics service
      this.fetchData = true;

      // Prepare arguments
      const chartRequest = _.cloneDeep(this.widget.chart.request);
      let field = this.widget.chart.request.field;
      if (this.widget.chart.request.aggs && this.widget.chart.request.aggs.includes('field:')) {
        field = this.widget.chart.request.aggs.replace('field:', '');
      }
      const queryFilters = this.AnalyticsService.getQueryFilters();
      if (queryFilters) {
        let filters;
        if (Object.keys(queryFilters).find((q) => q === field)) {
          filters = Object.keys(queryFilters).filter((q) => chartRequest.ranges || q !== field);
        } else {
          filters = Object.keys(queryFilters);
        }
        chartRequest.query = filters
          .map((f) => '(' + f + ':' + queryFilters[f].map((qp) => this.AnalyticsService.buildQueryParam(qp, f)).join(' OR ') + ')')
          .join(' AND ');
      }

      if (this.globalQuery) {
        if (chartRequest.query) {
          if (!_.includes(chartRequest.query, this.globalQuery)) {
            chartRequest.query += ' AND ' + this.globalQuery;
          }
        } else {
          chartRequest.query = this.globalQuery;
        }
      }

      const args = [this.widget.root, chartRequest];

      if (!this.widget.root) {
        args.splice(0, 1);
      }

      this.widget.chart.service.function
        .apply(this.widget.chart.service.caller, args)
        .then((response) => {
          this.results = response.data;
          if (this.widget.chart.type === 'line') {
            if (response.data.timestamp) {
              // call searchEvent only if there are some results to the aggregation call
              if (this.$state.params.apiId) {
                return this.ApiService.searchApiEvents(
                  ['PUBLISH_API'],
                  this.$state.params.apiId,
                  response.data.timestamp.from,
                  response.data.timestamp.to,
                  0,
                  10,
                ).then((response) => {
                  this.results.events = response.data;
                });
              } else {
                return this.EventsService.search(['PUBLISH_API'], [], response.data.timestamp.from, response.data.timestamp.to, 0, 10).then(
                  (response) => {
                    this.results.events = response.data;
                  },
                );
              }
            }
          } else if (this.widget.chart.percent) {
            delete chartRequest.query;
            chartRequest.type = 'count';
            return this.widget.chart.service.function.apply(this.widget.chart.service.caller, args).then((responseTotal) => {
              _.forEach(this.results.values, (value, key) => {
                this.results.values[key] = value + '/' + _.round((value / responseTotal.data.hits) * 100, 2);
              });
            });
          }
        })
        .finally(() => (this.fetchData = false));
    };

    this.delete = () => {
      $scope.$emit('onWidgetDelete', this.widget);
    };

    this.getIconFromType = () => {
      switch (this.widget.chart.type) {
        case 'line':
          return 'multiline_chart';
        case 'map':
          return 'map';
        case 'pie':
          return 'pie_chart';
        case 'stats':
          return 'bubble_chart';
        case 'table':
          return 'view_list';
        default:
          return 'insert_chart';
      }
    };
  },
};

export default WidgetComponent;
