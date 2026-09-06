import { Chart } from "react-google-charts";

export function DonutChart(props) {
  const statusMap = props.statusMap || {};
  const entries = Object.entries(statusMap);
  const numberOfColumns = entries.length;
  const totalTasks = entries.reduce((sum, [_, count]) => sum + count, 0);
  const chartData = [["Status", "Tasks", { role: "tooltip", type: "string" }]];
  const sliceColorMapping = {};
  if (totalTasks > 0) {
    entries.forEach(([columnName, taskCount], index) => {
      const displayValue = taskCount > 0 ? taskCount : 0.000001;
      const tooltipText = `${columnName}: ${taskCount}`;
      chartData.push([columnName, displayValue, tooltipText]);
      const hue = (index * (360 / numberOfColumns)) % 360;
      sliceColorMapping[index] = { color: hslToHex(hue, 65, 65) };
    });
  } else if (numberOfColumns > 0) {
    entries.forEach(([columnName, _], index) => {
      chartData.push([columnName, 1, `${columnName}: 0`]);
      const grayShade = index % 2 === 0 ? "#e9ecef" : "#dee2e6";
      sliceColorMapping[index] = { color: grayShade };
    });
  } else {
    chartData.push(["No Columns", 1, "No data available"]);
    sliceColorMapping[0] = { color: "#e9ecef" };
  }
  const options = {
    pieHole: 0.5,
    pieSliceText: "none",
    legend: {
      textStyle: { fontSize: 12, color: "#495057" },
    },
    slices: sliceColorMapping,
    sliceVisibilityThreshold: 0,
    tooltip: { trigger: totalTasks > 0 ? "focus" : "none" },
    chartArea: { left: "10%", top: "5%", width: "80%", height: "75%" },
  };
  return (
    <Chart
      chartType="PieChart"
      width="100%"
      height="100%"
      data={chartData}
      options={options}
    />
  );
}

function hslToHex(h, s, l) {
  l /= 100;
  const a = (s * Math.min(l, 1 - l)) / 100;
  const f = (n) => {
    const k = (n + h / 30) % 12;
    const color = l - a * Math.max(Math.min(k - 3, 9 - k, 1), -1);
    return Math.round(255 * color)
      .toString(16)
      .padStart(2, "0");
  };
  return `#${f(0)}${f(8)}${f(4)}`;
}
