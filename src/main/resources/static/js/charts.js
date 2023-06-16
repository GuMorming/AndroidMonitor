const memoryChart = echarts.init(document.getElementById("chart-memory-available"));
const memory = 1997;
let memoryAvailable = 576;
let memoryProportion = (memoryAvailable / memory).toFixed(2);
data = [
    {
        name: memoryAvailable + 'MB',
        value: memoryProportion
    }]
;
memoryChart.setOption({
    series: [{
        type: "liquidFill",
        name: "可用内存",
        data: data,
        label: {
            fontSize: 16
        },

        outline: {
            show: false
        },
    }],
    tooltip: {
        show: true
    },
});