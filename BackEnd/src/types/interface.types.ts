const ITYPES = {
  Service: Symbol.for("Service"),
  Repository: Symbol.for("Repository"),
  Controller: Symbol.for("Controller"),
  Datasource: Symbol.for("Datasource"),
  OtpTokenRepository: Symbol.for("OtpTokenRepository"), // Thêm dòng này
};

export { ITYPES };
