<template>
  <div class="parking-map-wrapper" ref="wrapperRef">
    <div ref="containerRef" class="parking-map-container"></div>
    <!-- 图例 -->
    <div class="map-legend">
      <span class="legend-item"><span class="dot free"></span> 空闲</span>
      <span class="legend-item"><span class="dot occupied"></span> 已占用</span>
      <span class="legend-item"><span class="dot applying"></span> 申请中</span>
      <span class="legend-item"><span class="dot mine"></span> 我的车位</span>
      <span class="legend-item"><span class="dot empty-cfg"></span> 未配置</span>
    </div>
    <!-- 操作提示 -->
    <div class="map-hint">滚轮缩放 | 拖拽移动 | 点击车位查看详情</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import Konva from 'konva'

const props = defineProps({
  spots: { type: Array, default: () => [] },
  applications: { type: Array, default: () => [] },
  myParkingId: { type: Number, default: null }
})

const emit = defineEmits(['spot-click'])

const wrapperRef = ref(null)
const containerRef = ref(null)
let stage = null
let spotsLayer = null
let bgLayer = null

// ============================
//  布局常量（参考真实停车场标准）
//  车位宽: 48px → 约 2.4m
//  车位长: 80px → 约 5.0m
//  车道宽: 68px → 约 6.0m
//  柱距:   44px → 约 8.0m (两柱之间3车位)
// ============================
const SPOT_W = 48
const SPOT_H = 80
const SPOT_GAP = 4
const LANE_H = 68
const COL_GAP = 44
const PADDING = 44
const SEC_GAP = 24  // 两段之间的背靠背间隙

// A/B 共用车道1, C/D 共用车道2
const ROW_DEFS = [
  { y: PADDING, count: 8, prefix: 'A', dir: 'down', laneIdx: 0 },
  { y: PADDING + SPOT_H + LANE_H, count: 8, prefix: 'B', dir: 'up', laneIdx: 0 },
  { y: PADDING + SPOT_H + LANE_H + SPOT_H + SEC_GAP, count: 8, prefix: 'C', dir: 'down', laneIdx: 1 },
  { y: PADDING + SPOT_H + LANE_H + SPOT_H + SEC_GAP + SPOT_H + LANE_H, count: 8, prefix: 'D', dir: 'up', laneIdx: 1 },
]

const MID_POINT = Math.floor(ROW_DEFS[0].count / 2) // 4 — 柱子分隔位置
const TOTAL_W = PADDING * 2 + ROW_DEFS[0].count * (SPOT_W + SPOT_GAP) - SPOT_GAP + COL_GAP
const TOTAL_H = ROW_DEFS[ROW_DEFS.length - 1].y + SPOT_H + PADDING

// ============================
//  数据映射工具
// ============================
function getApplicationMap() {
  const map = {}
  if (props.applications) {
    props.applications.forEach(app => {
      if (app.status === 0) map[app.parkingId] = app
    })
  }
  return map
}

function getSpotMap() {
  const map = {}
  props.spots.forEach(s => { map[s.id] = s })
  return map
}

function normalizeNumber(num) {
  const match = num.match(/^([A-Za-z]+)[-\s]*(\d+)$/)
  if (match) {
    return { prefix: match[1].toUpperCase(), num: parseInt(match[2], 10) }
  }
  return null
}

function findSpotByNumber(number, spotMap) {
  const norm = normalizeNumber(number)
  if (!norm) return null
  for (const key of Object.keys(spotMap)) {
    const s = spotMap[key]
    const sNorm = normalizeNumber(s.number)
    if (sNorm && sNorm.prefix === norm.prefix && sNorm.num === norm.num) return s
  }
  return null
}

function buildSpotsData() {
  const spotMap = getSpotMap()
  const appMap = getApplicationMap()
  const result = []

  ROW_DEFS.forEach((rowDef, rowIdx) => {
    for (let col = 0; col < rowDef.count; col++) {
      const colOffset = col >= MID_POINT ? COL_GAP : 0
      const x = PADDING + col * (SPOT_W + SPOT_GAP) + colOffset
      const y = rowDef.y
      const label = `${rowDef.prefix}${String(col + 1).padStart(2, '0')}`

      const spot = findSpotByNumber(label, spotMap)

      let status = 'empty'
      let spotData = null
      let isApplying = false
      let isMine = false

      if (spot) {
        spotData = spot
        if (props.myParkingId && spot.id === props.myParkingId) {
          status = 'mine'
          isMine = true
        } else if (appMap[spot.id]) {
          status = 'applying'
          isApplying = true
        } else if (spot.status === 1) {
          status = 'occupied'
        } else {
          status = 'free'
        }
      }

      result.push({
        id: spot ? spot.id : `empty-${rowIdx}-${col}`,
        number: spot ? spot.number : label,
        location: spot ? spot.location : '',
        status, x, y, rowDef, spotData, isApplying, isMine,
        application: appMap[spot?.id] || null
      })
    }
  })

  return result
}

// ============================
//  颜色方案：淡色底 + 同色调深色边框
// ============================
function getSpotColors(item) {
  // status: 'mine' | 'free' | 'occupied' | 'applying' | 'empty'
  const palette = {
    mine:      { fill: '#dbeafe', stroke: '#2563eb', dotFill: '#3b82f6' },
    free:      { fill: '#e8f5e9', stroke: '#43a047', dotFill: '#66bb6a' },
    occupied:  { fill: '#ffebee', stroke: '#e53935', dotFill: '#ef5350' },
    applying:  { fill: '#fff8e1', stroke: '#fb8c00', dotFill: '#ffa726' },
    empty:     { fill: '#f5f5f5', stroke: '#bdbdbd', dotFill: '#bdbdbd' },
  }
  return palette[item.status] || palette.empty
}

// ============================
//  背景绘制
// ============================
function drawBackground() {
  bgLayer.destroyChildren()

  // —— 地面 ——————————————————————
  bgLayer.add(new Konva.Rect({
    x: 0, y: 0, width: TOTAL_W, height: TOTAL_H,
    fill: '#3d3d3d',
    cornerRadius: 6,
  }))

  // —— 车道（深色路面） ——————————
  const laneDefs = getLaneRegions()
  laneDefs.forEach(lane => {
    bgLayer.add(new Konva.Rect({
      x: PADDING - 8, y: lane.y,
      width: TOTAL_W - PADDING * 2 + 16,
      height: lane.h,
      fill: '#4a4a4a',
      cornerRadius: 3,
    }))
  })

  // —— 车道中心虚线 + 方向标记 ——
  laneDefs.forEach((lane, li) => {
    const cy = lane.y + lane.h / 2
    bgLayer.add(new Konva.Line({
      points: [PADDING + 10, cy, TOTAL_W - PADDING - 10, cy],
      stroke: '#fdd835',
      strokeWidth: 2,
      dash: [14, 10],
      lineCap: 'round',
    }))
    const dirLabel = li === 0 ? '← 车道 →' : '← 车道 →'
    bgLayer.add(new Konva.Text({
      x: PADDING + 20, y: cy - 9,
      text: dirLabel,
      fontSize: 11,
      fill: 'rgba(255,255,255,0.4)',
      fontStyle: 'italic',
    }))
  })

  // —— 车位白线（地面标线） ————————
  const spotsData = buildSpotsData()
  spotsData.forEach(item => {
    bgLayer.add(new Konva.Rect({
      x: item.x - 1, y: item.y - 1,
      width: SPOT_W + 2, height: SPOT_H + 2,
      stroke: 'rgba(255,255,255,0.5)',
      strokeWidth: 1,
      dash: [6, 3],
      fill: 'transparent',
      listening: false,
    }))
  })

  // —— 柱子（结构柱，跨全高） ——————
  const pillarX = PADDING + MID_POINT * (SPOT_W + SPOT_GAP)
  const pillarW = COL_GAP

  laneDefs.forEach(lane => {
    bgLayer.add(new Konva.Rect({
      x: pillarX, y: lane.y - 2,
      width: pillarW, height: lane.h + 4,
      fill: '#757575',
      cornerRadius: 2,
      stroke: '#616161',
      strokeWidth: 1,
    }))
    // 柱体黄色警示角标
    const cornerSize = 10
    const corners = [
      [pillarX, lane.y - 2],
      [pillarX + pillarW - cornerSize, lane.y - 2],
      [pillarX, lane.y + lane.h + 2 - cornerSize],
      [pillarX + pillarW - cornerSize, lane.y + lane.h + 2 - cornerSize],
    ]
    corners.forEach(([cx, cy]) => {
      bgLayer.add(new Konva.Rect({
        x: cx, y: cy,
        width: cornerSize, height: cornerSize,
        fill: '#fdd835',
        listening: false,
      }))
    })
  })

  // 背靠背间隙中的柱体连接件
  const backY1 = ROW_DEFS[0].y + SPOT_H + LANE_H + SPOT_H
  const backY2 = ROW_DEFS[2].y
  if (backY2 - backY1 > 8) {
    bgLayer.add(new Konva.Rect({
      x: pillarX + 4, y: backY1 + 2,
      width: pillarW - 8, height: backY2 - backY1 - 4,
      fill: '#757575',
      cornerRadius: 1,
      stroke: '#616161',
      strokeWidth: 1,
    }))
  }

  // —— 墙体（上下边界） ——————————
  bgLayer.add(new Konva.Rect({
    x: -3, y: -3,
    width: TOTAL_W + 6, height: PADDING - 28,
    fill: '#5d5d5d',
    cornerRadius: 3,
  }))
  drawEntryArrow(TOTAL_W / 2, PADDING - 14, true)

  bgLayer.add(new Konva.Rect({
    x: -3, y: TOTAL_H - PADDING + 25,
    width: TOTAL_W + 6, height: PADDING - 28,
    fill: '#5d5d5d',
    cornerRadius: 3,
  }))
  drawEntryArrow(TOTAL_W / 2, TOTAL_H - PADDING + 36, false)

  // —— 入口/出口标签 ——
  bgLayer.add(new Konva.Text({
    x: PADDING - 20, y: PADDING - 36,
    text: '入口',
    fontSize: 13, fontStyle: 'bold',
    fill: '#e0e0e0',
  }))
  bgLayer.add(new Konva.Text({
    x: PADDING - 20, y: TOTAL_H - PADDING + 28,
    text: '出口',
    fontSize: 13, fontStyle: 'bold',
    fill: '#e0e0e0',
  }))

  bgLayer.draw()
}

function getLaneRegions() {
  const lanes = []
  for (let i = 0; i < 2; i++) {
    const topRow = ROW_DEFS[i * 2]
    const botRow = ROW_DEFS[i * 2 + 1]
    const laneY = topRow.y + SPOT_H + 4
    const laneH = botRow.y - laneY
    if (laneH > 30) lanes.push({ y: laneY, h: laneH })
  }
  return lanes
}

function drawEntryArrow(cx, baseY, isEntry) {
  const arrowY = isEntry ? baseY - 8 : baseY - 12
  const size = 16
  if (isEntry) {
    bgLayer.add(new Konva.Line({
      points: [cx, arrowY, cx - size / 2, arrowY + size, cx + size / 2, arrowY + size],
      closed: true,
      fill: '#a5d6a7',
      stroke: '#66bb6a',
      strokeWidth: 1.5,
      listening: false,
    }))
  } else {
    bgLayer.add(new Konva.Line({
      points: [cx, arrowY + size, cx - size / 2, arrowY, cx + size / 2, arrowY],
      closed: true,
      fill: '#ef9a9a',
      stroke: '#ef5350',
      strokeWidth: 1.5,
      listening: false,
    }))
  }
}

// ============================
//  车位绘制
// ============================
function drawSpots() {
  spotsLayer.destroyChildren()
  const data = buildSpotsData()

  data.forEach(item => {
    const colors = getSpotColors(item)

    const group = new Konva.Group({
      x: item.x, y: item.y,
      name: 'spot-group',
    })

    // —— 车位底色矩形（淡色填充 + 同色调深色边框） ——
    const rect = new Konva.Rect({
      width: SPOT_W,
      height: SPOT_H,
      fill: colors.fill,
      stroke: item.isMine ? colors.stroke : colors.stroke,
      strokeWidth: item.isMine ? 3 : 2,
      cornerRadius: 3,
      name: 'spot-rect',
    })

    // 四个角小色块
    const cw = 6, ch = 6
    const cornerPositions = [
      [0, 0], [SPOT_W - cw, 0],
      [0, SPOT_H - ch], [SPOT_W - cw, SPOT_H - ch],
    ]
    const cornerRects = cornerPositions.map(([cx, cy]) =>
      new Konva.Rect({
        x: cx, y: cy,
        width: cw, height: ch,
        fill: colors.stroke,
        cornerRadius: 2,
        opacity: 0.35,
        listening: false,
      })
    )

    // —— 车位号标签 ——
    const label = new Konva.Text({
      text: item.number,
      fontSize: 10,
      fontStyle: 'bold',
      fill: colors.stroke,
      width: SPOT_W,
      align: 'center',
      y: SPOT_H / 2 - 18,
      name: 'spot-label',
      listening: false,
    })

    // —— 状态图标 ——
    const statusTextMap = {
      mine: '[我]',
      occupied: '[占]',
      applying: '[...]',
      free: '[空]',
      empty: '',
    }
    const statusIcon = new Konva.Text({
      text: statusTextMap[item.status] || '',
      fontSize: 20,
      width: SPOT_W,
      align: 'center',
      y: SPOT_H / 2,
      name: 'spot-icon',
      listening: false,
    })

    // 状态文字
    const statusLabelMap = {
      mine: '我的',
      free: '空闲',
      occupied: '占用',
      applying: '申请中',
      empty: '未配置',
    }
    const statusText = new Konva.Text({
      text: statusLabelMap[item.status] || '',
      fontSize: 9,
      fill: colors.stroke,
      width: SPOT_W,
      align: 'center',
      y: SPOT_H - 15,
      fontStyle: 'italic',
      name: 'spot-status-text',
      listening: false,
    })

    group.add(rect)
    cornerRects.forEach(r => group.add(r))
    group.add(label)
    group.add(statusIcon)
    group.add(statusText)

    // ========================
    //  交互（业主端）
    // ========================
    const isClickable = !!item.spotData
    if (isClickable) {
      const hoverColor = item.isMine ? colors.stroke : (item.isApplying ? '#fb8c00' : '#2563eb')

      group.on('mouseenter', () => {
        document.body.style.cursor = 'pointer'
        rect.shadowColor(hoverColor)
        rect.shadowBlur(14)
        rect.shadowOffset({ x: 0, y: 0 })
        rect.shadowOpacity(0.7)
        rect.scaleX(1.08)
        rect.scaleY(1.08)
        rect.strokeWidth(item.isMine ? 4 : 3)
        rect.stroke(hoverColor)
        spotsLayer.draw()
      })
      group.on('mouseleave', () => {
        document.body.style.cursor = 'default'
        rect.shadowColor('transparent')
        rect.shadowBlur(0)
        rect.shadowOpacity(0)
        rect.scaleX(1)
        rect.scaleY(1)
        rect.strokeWidth(item.isMine ? 3 : 2)
        rect.stroke(colors.stroke)
        spotsLayer.draw()
      })
      group.on('click', () => {
        emit('spot-click', { spot: item.spotData, application: item.application, mapItem: item })
      })
    }

    spotsLayer.add(group)
  })

  spotsLayer.draw()
}

// ============================
//  Stage 初始化 & 居中
// ============================
function initStage() {
  if (!containerRef.value) return

  const wrapper = wrapperRef.value
  const wrapperWidth = wrapper?.clientWidth || 1000

  const scale = Math.min((wrapperWidth - 20) / TOTAL_W, 1)

  const contentW = TOTAL_W * scale
  const contentH = TOTAL_H * scale
  const stageH = Math.min(contentH + 20, 700)

  stage = new Konva.Stage({
    container: containerRef.value,
    width: wrapperWidth,
    height: stageH,
    draggable: true,
  })

  bgLayer = new Konva.Layer()
  spotsLayer = new Konva.Layer()

  stage.add(bgLayer)
  stage.add(spotsLayer)

  stage.scale({ x: scale, y: scale })

  // 第一次加载居中
  const posX = contentW < wrapperWidth ? (wrapperWidth - contentW) / 2 : 0
  const posY = contentH < stageH ? (stageH - contentH) / 2 : 0
  stage.position({ x: posX, y: posY })

  // 滚轮缩放（以鼠标为中心）
  stage.on('wheel', (e) => {
    e.evt.preventDefault()
    const oldScale = stage.scaleX()
    const pointer = stage.getPointerPosition()

    const scaleBy = 1.08
    const newScale = e.evt.deltaY > 0 ? oldScale / scaleBy : oldScale * scaleBy
    const clamped = Math.max(0.35, Math.min(3, newScale))

    const mousePointTo = {
      x: (pointer.x - stage.x()) / oldScale,
      y: (pointer.y - stage.y()) / oldScale,
    }

    stage.scale({ x: clamped, y: clamped })
    stage.position({
      x: pointer.x - mousePointTo.x * clamped,
      y: pointer.y - mousePointTo.y * clamped,
    })
    stage.batchDraw()
  })

  drawBackground()
  drawSpots()
}

function handleResize() {
  if (!stage || !wrapperRef.value) return
  stage.width(wrapperRef.value.clientWidth)
  stage.draw()
}

onMounted(() => {
  nextTick(() => {
    initStage()
    window.addEventListener('resize', handleResize)
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (stage) { stage.destroy(); stage = null }
})

watch(() => [props.spots, props.applications, props.myParkingId], () => {
  if (stage) { drawSpots(); drawBackground() }
}, { deep: true })
</script>

<style scoped>
.parking-map-wrapper {
  width: 100%;
  position: relative;
  background: #1a1a2e;
  border-radius: 8px;
  overflow: hidden;
}

.parking-map-container {
  width: 100%;
  cursor: grab;
}

.parking-map-container:active {
  cursor: grabbing;
}

.map-legend {
  position: absolute;
  bottom: 36px;
  right: 16px;
  background: rgba(0, 0, 0, 0.75);
  padding: 8px 14px;
  border-radius: 6px;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  z-index: 10;
  backdrop-filter: blur(4px);
}

.legend-item {
  color: #ccc;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 3px;
  display: inline-block;
  border: 1px solid rgba(0,0,0,0.25);
}

.dot.free      { background: #e8f5e9; border-color: #43a047; }
.dot.occupied  { background: #ffebee; border-color: #e53935; }
.dot.applying  { background: #fff8e1; border-color: #fb8c00; }
.dot.mine      { background: #dbeafe; border-color: #2563eb; }
.dot.empty-cfg { background: #f5f5f5; border-color: #bdbdbd; }

.map-hint {
  position: absolute;
  bottom: 10px;
  left: 14px;
  color: rgba(255,255,255,0.45);
  font-size: 11px;
  z-index: 10;
  pointer-events: none;
}
</style>
