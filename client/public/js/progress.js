var timeProgressInterval = null;

var progress2 = function() {
    var w = 300, h = 320;
    var outerRadius = (w / 2) - 10;
    var innerRadius = outerRadius - 8;
    var color = ['#1AB394', '#2a3a46', '#202b33'];
    var arc = d3.svg.arc()
        .innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .startAngle(0)
        .endAngle(2 * Math.PI);

//The circle is following this
    var arcDummy = d3.svg.arc()
        .innerRadius((outerRadius - innerRadius) / 2 + innerRadius)
        .outerRadius((outerRadius - innerRadius) / 2 + innerRadius)
        .startAngle(0);

    var arcLine = d3.svg.arc()
        .innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .startAngle(0);

    var svg = d3.select("#chart").append("svg").attr({
        width: w,
        height: h
    }).append('g').attr({
        // transform: 'translate(' + (w / 2) + ',' + (h / 2) + ')'
        transform: 'translate(' + 152 + ',' + 152 + ')'
    });

//background
    var path = svg.append('path').attr({d: arc}).style({
        fill: color[1]
    });

    var pathForeground = svg.append('path').datum({
        endAngle: 0
    }).attr({
        d: arcLine
    }).style({
        fill: color[0]
    });

    var endCircle = svg.append('circle').attr({
        r: 12,
        transform: 'translate(0,' + (-outerRadius + 4) + ')'
    }).style({
        stroke: color[0],
        'stroke-width': 8,
        fill: color[2]
    });

    var middleTextCount = svg.append('text').datum(0).text(function(d) {
        return d + '%';
    }).attr({
        class: 'middleText',
        'text-anchor': 'middle',
        id: "progress",
        dy: 25,
        dx: 0
    }).style({
        fill: '#1AB394',
        'font-size': '80px'
    });

    var arcTweenOld = function(transition, percent, oldValue) {
        transition.attrTween("d", function(d) {

            var newAngle = (percent / 100) * (2 * Math.PI);

            var interpolate = d3.interpolate(d.endAngle, newAngle);

            var interpolateCount = d3.interpolate(oldValue, percent);

            return function(t) {
                d.endAngle = interpolate(t);
                var pathForegroundCircle = arcLine(d);
                middleTextCount.text((Math.floor(interpolateCount(t))) + '%');
                var pathDummyCircle = arcDummy(d);
                try {
                    var coordinate = pathDummyCircle.split("L")[1].split("A")[0];
                    endCircle.attr('transform', 'translate(' + coordinate + ')');
                } catch(e) {
                    var t = "-0.539510655986597,-135.73163506624493"
                    endCircle.attr('transform', 'translate(' + t + ')');
                }
                return pathForegroundCircle;
            };
        });
    };

    this.oldValue = 0;
    this.setPercent = function(num) {
        var t = 0
        if(num > 0 && this.oldValue < 100) {
            t = this.oldValue + num;
        } if(this.oldValue >= 100 || t >= 100) {
            t = 100;
            this.oldValue = 100;
        } if(num == 0) {
            t = 0
            this.oldValue = 0
        }

        pathForeground.transition()
            .duration(750)
            .ease('cubic')
            .call(arcTweenOld, t, this.oldValue);

        this.oldValue = t;
        return this.oldValue;
    }
}

var setProgressStart = function(time) {
    var temp = msgIdentifying;
    var setP = function() {
        if(msgIdentifying == temp) {
            p.setPercent(1);
        }else {
            setTimeout(function(){setProgressStop(time)}, 1000 * 3);
        }
    }
    timeProgressInterval = setInterval(setP, time);
}

var setProgressStop = function(time) {
    if(timeProgressInterval) {
        clearInterval(timeProgressInterval);
        timeProgressInterval = null;
        setTimeout(function(){setProgressStart(time);}, 1000 * 30);
    }
}

var setCloseInterval = function() {
    p.setPercent(0);
    if(timeProgressInterval) {
        clearInterval(timeProgressInterval);
        timeProgressInterval = null;
    }
}