export interface Device {
  deviceId: string
  name: string
  label: string
  manufacturerCode: string | null
  typeName: string | null
  type: string | null
  roomId: string | null
  locationId: string | null
}
