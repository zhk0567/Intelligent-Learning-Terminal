Component({
  options: {
    multipleSlots: false,
    addGlobalClass: true,
  },
  properties: {
    open: { type: Boolean, value: false },
    title: { type: String, value: "" },
    maxHeight: { type: String, value: "70vh" },
  },
  methods: {
    onClose() {
      this.triggerEvent("close", {});
    },
    noop() {},
  },
});
