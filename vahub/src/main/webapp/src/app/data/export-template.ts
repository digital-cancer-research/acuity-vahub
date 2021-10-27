/*
 * Copyright 2021 The University of Manchester
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

export const html = `
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        * {
            font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
            margin: 0;
            padding: 0;
            outline: 0;
            border: none;
        }

        body {
            min-width: 500px;
            padding: 0 16px;
        }

        h1 {
            font-size: 16px;
            margin: 16px 0;
        }

        p {
            margin: 16px 0;
        }

        li {
            list-style: none;
        }

        #titles {
            text-align: center;
        }

        #content {
            display: flex;
            width: 100%;
            max-width: 100%;
        }

        #chart {
            flex: 1 0 auto;
            height: 100%
            margin: 4px;
        }

        #chart.portrait {
            width: <%= chartWidth %>px;
        }

        #chart.landscape {
            width: 100%;
        }

        #chart img,
        #chart svg {
            width: 100%;
            max-width: 100%;
        }

        #notification {
            margin-right: 16px;
            color: #d6d6d6;
            text-align: right;
        }

        #parameters.row {
            margin: 0 16px;
        }

        .row {
            display: flex;
        }

        .column {
            display: flex;
            flex-direction: column;
        }

        @page {
            margin: 0;
        }

        .axis-control {
            display: none;
        }

}
    </style>
</head>
<body>
    <div id="titles">
        <h1><%- title %></h1>
        <p><%- subtitle %></p>
    </div>
    <div id="content" class="<%= orientation === 'portrait' ? 'row' : 'column' %>">
        <% if (_.isArray(chart)) { %>
            <div id="chart" class="<%= orientation === 'portrait' ? 'portrait' : 'landscape' %>">
                <% chart.forEach((chartItem, index) => { %>
                    <% if (titlesArray) { %>
                        <div><%= titlesArray[index] %></div>
                    <% } %>
                    <div>
                        <%= chartItem %>
                    </div>
                <% }); %>
                <p id="notification"><%- notification %></p>
                <p id="info"><%- info %></p>
            </div>
        <% } else { %>
            <div id="chart" class="<%= orientation === 'portrait' ? 'portrait' : 'landscape' %>">
                <%= chart %>
                <p id="notification"><%- notification %></p>
            </div>
        <% } %>
    </div>
</body>
</html>
`;
